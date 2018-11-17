package com.xiangrikui.api.mc.common.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

/**
 * 扫描指定包下的所有类
 */
public class ClassHelper
{
	public static List<String> getParameterNames(Method method)
	{
		Parameter[] parameters = method.getParameters();
		List<String> parameterNames = new ArrayList<>();

		for (Parameter parameter : parameters) {
			if (!parameter.isNamePresent()) {
				throw new IllegalArgumentException("Parameter names are not present!");
			}

			String parameterName = parameter.getName();
			parameterNames.add(parameterName);
		}
		return parameterNames;
	}

	public static List<String> getParameterNamesByAsm(Method m) throws NotFoundException
	{
		ClassPool pool = ClassPool.getDefault();
		CtClass cc = pool.get(m.getDeclaringClass().getName());

		CtMethod[] aryM = cc.getDeclaredMethods(m.getName());
		CtMethod cm = aryM[0];
		// 检测重载方法
		if (aryM.length > 1) {
			for (CtMethod temp : aryM) {
				if (temp.getName().equals(m.getName())
				        && temp.getParameterTypes().length == m.getParameterTypes().length) {
					cm = temp;
					break;
				}
			}
		}

		// 使用javaassist的反射方法获取方法的参数名
		MethodInfo methodInfo = cm.getMethodInfo();
		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
		LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
		        .getAttribute(LocalVariableAttribute.tag);
		if (attr == null) {
			// exception
		}

		List<String> parameterNames = new ArrayList<>();
		int len = cm.getParameterTypes().length;
		int pos = 0;

		if (!Modifier.isStatic(cm.getModifiers())) {
			for (int i = 0, count = attr.tableLength(); i < count; ++i) {
				if (attr.variableName(i).equalsIgnoreCase("this")) {
					pos = i + 1;
					break;
				}
			}
		}

		for (int i = 0; i < len; i++)
			parameterNames.add(attr.variableName(i + pos));
		return parameterNames;
	}

	/**
	 * 
	 * 从指定包扫描所有类，默认递归扫描
	 * 
	 * @param pack
	 * @return
	 */
	public static Set<Class<?>> getClasses(String pack)
	{
		return getClasses(pack, true);
	}

	/**
	 * 从包package中获取所有的Class
	 * 
	 * @param pack
	 * @param recursive
	 * @return
	 */
	public static Set<Class<?>> getClasses(String pack, boolean recursive)
	{
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		// 获取包的名字 并进行替换
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
				}
				else if ("jar".equals(protocol)) {
					JarFile jar;
					try {
						// 获取jar
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
						Enumeration<JarEntry> entries = jar.entries();
						while (entries.hasMoreElements()) {
							// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							if (name.charAt(0) == '/') {
								name = name.substring(1);
							}
							if (name.startsWith(packageDirName)) {
								int idx = name.lastIndexOf('/');
								if (idx != -1) {
									packageName = name.substring(0, idx).replace('/', '.');
								}
								if ((idx != -1) || recursive) {
									if (name.endsWith(".class") && !entry.isDirectory()) {
										String className = name.substring(packageName.length() + 1,
										        name.length() - 6);
										try {
											classes.add(Class
											        .forName(packageName + '.' + className));
										}
										catch (ClassNotFoundException e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return classes;
	}

	/**
	 * 以文件的形式来获取包下的所有Class
	 * 
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
	                                                     final boolean recursive,
	                                                     Set<Class<?>> classes)
	{
		File dir = new File(packagePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] dirfiles = dir.listFiles(new FileFilter() {
			public boolean accept(File file)
			{
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		for (File file : dirfiles) {
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
				        file.getAbsolutePath(), recursive, classes);
			}
			else {
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					classes.add(Thread.currentThread().getContextClassLoader()
					        .loadClass(packageName + '.' + className));
				}
				catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 * 在当前类中查找指定的类型，如：模板参数类型
	 * 
	 * @param object
	 * @param parameterizedSuperclass
	 * @param typeParamName
	 * @return
	 */
	public static Class<?> find(final Object object, Class<?> parameterizedSuperclass,
	                            String typeParamName)
	{
		final Class<?> thisClass = object.getClass();
		Class<?> currentClass = thisClass;
		for (;;) {
			if (currentClass.getSuperclass() == parameterizedSuperclass) {
				int typeParamIndex = -1;
				TypeVariable<?>[] typeParams = currentClass.getSuperclass().getTypeParameters();
				for (int i = 0; i < typeParams.length; i++) {
					if (typeParamName.equals(typeParams[i].getName())) {
						typeParamIndex = i;
						break;
					}
				}

				if (typeParamIndex < 0) {
					throw new IllegalStateException("unknown type parameter '" + typeParamName
					        + "': " + parameterizedSuperclass);
				}

				Type genericSuperType = currentClass.getGenericSuperclass();
				if (!(genericSuperType instanceof ParameterizedType)) {
					return Object.class;
				}

				Type[] actualTypeParams = ((ParameterizedType) genericSuperType)
				        .getActualTypeArguments();

				Type actualTypeParam = actualTypeParams[typeParamIndex];
				if (actualTypeParam instanceof ParameterizedType) {
					actualTypeParam = ((ParameterizedType) actualTypeParam).getRawType();
				}
				if (actualTypeParam instanceof Class) {
					return (Class<?>) actualTypeParam;
				}
				if (actualTypeParam instanceof GenericArrayType) {
					Type componentType = ((GenericArrayType) actualTypeParam)
					        .getGenericComponentType();
					if (componentType instanceof ParameterizedType) {
						componentType = ((ParameterizedType) componentType).getRawType();
					}
					if (componentType instanceof Class) {
						return Array.newInstance((Class<?>) componentType, 0).getClass();
					}
				}
				if (actualTypeParam instanceof TypeVariable) {
					// Resolved type parameter points to another type parameter.
					TypeVariable<?> v = (TypeVariable<?>) actualTypeParam;
					currentClass = thisClass;
					if (!(v.getGenericDeclaration() instanceof Class)) {
						return Object.class;
					}

					parameterizedSuperclass = (Class<?>) v.getGenericDeclaration();
					typeParamName = v.getName();
					if (parameterizedSuperclass.isAssignableFrom(thisClass)) {
						continue;
					}
					else {
						return Object.class;
					}
				}

				return fail(thisClass, typeParamName);
			}
			currentClass = currentClass.getSuperclass();
			if (currentClass == null) {
				return fail(thisClass, typeParamName);
			}
		}
	}

	private static Class<?> fail(Class<?> type, String typeParamName)
	{
		throw new IllegalStateException("cannot determine the type of the type parameter '"
		        + typeParamName + "': " + type);
	}
}
