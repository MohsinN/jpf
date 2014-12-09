import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import annotations.UmlSpec;

import checking.CallListener;
import gov.nasa.jpf.JPF;

import sequence_diagram.SequenceDiagram;
/**
 * Main class for run the program
 * @author Phuc Nguyen Dinh
 * @author Chung Tuyen Luu
 *
 */
public class RunProtocolChecking implements Runnable{
	String [] jpfArgs;
	Hashtable<String, String> protocols;
	Vector<CallListener> listener;
	ArrayList<String> implementedClassNames;
	boolean verboseMode;
	public RunProtocolChecking(String [] args) {
		protocols = new Hashtable<String, String>();
		listener = new Vector<CallListener>();
		implementedClassNames = new ArrayList<String>();
		verboseMode = false;
		initAnnotation(args);
		//init(args);
	}
	public static void main(String [] args) {
		if(args.length == 0 || args[0].equals("-help") ||args[0].equals("-h"))
			showHelp();
		/*else if (args.length == 2){
			if (args[1].equals("-verbose") ||args[1].equals("-v")){
				verboseMode = true;
			}
		}*/
		RunProtocolChecking checking = new RunProtocolChecking(args);
		checking.run();
		}
	
	private static void showHelp() {
		String helpString = "\nUsage: \njava Run [-help] " +
				"app-class [app-args...] -xmi xmiPath xmiPath..." +
				"\n\nwhere: \n" +
				"    -help         print this help message\n" +
				"    app-class     the class file that implement diagrams\n" +
				"    -xmi          paths to xmi sequence diagram files\n" +
				"	 [-v]			verbose mode"	;
		System.out.println(helpString);
		System.exit(0);
	}

	@Override
	public void run() {
		JPF jpf = new JPF(jpfArgs);
		Enumeration<String> methods = protocols.keys();
		while(methods.hasMoreElements()) {
			String method = methods.nextElement();
			if(!(new File(protocols.get(method)).exists())) {
				System.err.println("the xmi file is not exists !");
				System.exit(1);
			}
			CallListener listener = new CallListener(jpf, new SequenceDiagram(protocols.get(method)), method,verboseMode);
			jpf.addPropertyListener(listener);
		}
		jpf.run();
	}
	/*
	private void init(String [] args) {
		int i = 0;
		while(i < args.length && !args[i].equals("-xmi"))
			i++;
		if(i == args.length || !args[i].equals("-xmi")) {
			System.err.println("No protocol specified !");
			System.exit(1);
		}
		jpfArgs = new String[i + 2];
		jpfArgs[0] = "+vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory";
		jpfArgs[1] = "+report.console.finished=";
		System.arraycopy(args, 0, jpfArgs, 2, i);
		i++;
		while(i < args.length) {
			protocols.add(args[i]);
			i++;
		}
	}
	
	/*
	 * args is only as following:
	 * AppName
	 */
	private void initAnnotation(String [] args) {
		String symMethod = "+symbolic.method=";
		File userDir = new File(System.getProperty("user.dir"));
		getImplementedClasses(userDir);
		String [] classes;
		if (args.length >= 2){
			if (args[args.length-1].equals("-verbose")||args[args.length-1].equals("-v"))
				verboseMode = true;
			classes = new String[args.length -1];
			System.arraycopy(args, 0, classes, 0, args.length-1);
		}
		else classes = args;
		
		try {
			for(String clazz: classes) { 
			for(Method m: Class.forName(clazz).getMethods()) {
				if(m.isAnnotationPresent(UmlSpec.class)) {
					UmlSpec protocol = m.getAnnotation(UmlSpec.class);
					for(String sd: protocol.sequenceDiagram())
						protocols.put(clazz + "." + m.getName(), sd);
					if(symMethod.endsWith(")"))
						symMethod = symMethod + ",";
					symMethod = symMethod + clazz + "." + m.getName() + "(";
					Type[] parameterTypes = m.getGenericParameterTypes();
					for(Type type: parameterTypes) {
						if(isSupportedType(type.toString()))
								symMethod = symMethod + "sym#";
						else
							symMethod = symMethod + "con#";
					}
					if (symMethod.endsWith("#"))
						symMethod = symMethod.substring(0, symMethod.length() - 1);
					symMethod = symMethod + ")";
				}
			}
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		jpfArgs = new String [3 + args.length];	
		jpfArgs[0] = "+vm.insn_factory.class=gov.nasa.jpf.symbc.SymbolicInstructionFactory";
		jpfArgs[1] = "+report.console.finished=";
		jpfArgs[2] = symMethod;
		System.arraycopy(args, 0, jpfArgs, 3, args.length);
		
		
	}
	
	private boolean isSupportedType(String type) {
		if (type.equalsIgnoreCase("int")
				|| type.equalsIgnoreCase("float")
				|| type.equalsIgnoreCase("long")
				|| type.equalsIgnoreCase("double")
				|| type.equalsIgnoreCase("short")
				|| type.equalsIgnoreCase("byte")
				|| type.equalsIgnoreCase("string")
				|| type.equalsIgnoreCase("char")
				|| type.equalsIgnoreCase("boolean"))
			return true;
		return false;
	}
	
	private void getImplementedClasses(File searchDir) {
		File [] listFile = searchDir.listFiles();
		
		for(File classFile: listFile) 
			if(classFile.isFile() && classFile.getName().endsWith(".class")) {
				String fileName = classFile.getName().replace(".class", "");
				implementedClassNames.add(fileName);
			}
			else if(classFile.isDirectory())
				getImplementedClasses(classFile);		
	}
	
	private static ClassLoader getClassLoader(String classPath) {
		StringTokenizer token = new StringTokenizer(classPath, File.pathSeparator);
		Vector<File> files = new Vector<File>();
		File newFile;
		while(token.hasMoreTokens()) {
			newFile = new File(token.nextToken());
			if(!newFile.exists()) {
				System.out.println("The classpath is not exits!");
			}
			files.add(newFile);
		}
		URL [] urls = new URL[files.size()];
		for(int i = 0; i < files.size(); i++) {
			try {
				urls[i] = files.get(i).toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return new URLClassLoader(urls);
	}
}