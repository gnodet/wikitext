package org.eclipse.mylar.aspectj.util;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MakeDebug {
  
  public static void main(String[] args) throws Exception {
    new MakeDebug().realMain(args);
  }

  public void realMain(String[] args) throws Exception {
    for (int i=0; i<args.length; i++) process(Class.forName(args[i]));
  }

  private PrintStream out = System.out;

  public void process(Class cls) throws Exception {
    List methods = getMethods(cls);
    String newName = "Debugged" + simpleName(cls);
    if (cls.isInterface()) {
    	out.println("abstract class " + newName + " implements " + getName(cls) + " {");
    } else {
    	out.println("public class " + newName + " extends " + getName(cls) + " {");    	
    }
    out.println();
    out.println(heading("Interface"));
    for (Iterator it = methods.iterator(); it.hasNext();) {
      Method m = (Method)it.next();
      String s = "  public final ";
      Class retType = m.getReturnType();
      s += getName(retType);
      s += " ";
      s += m.getName() + "(";
      final String newMethodName = m.getName() + (cls.isInterface() ? "Rest" : "");
      String argString = "";
      Class[] pTypes = m.getParameterTypes();
      for (int i=0, N=pTypes.length; i<N; i++) {
      	argString += getName(pTypes[i]) + " arg" + i;
      	if (i<N-1) argString += ",";
      }
      s += argString;
      s += ") {";
      out.println(s);
      s = "    ";
      Object retValue = returnValue(retType);
      if (retValue != null) {
      	s += getName(retType) + " val = ";
      }

      if (!cls.isInterface()) {
      	s += "super.";
      }
      s += newMethodName + "(";
      for (int i=0, N=pTypes.length; i<N; i++) {
      	s += "arg" + i;
      	if (i<N-1) s += ",";
      }
      s += ");";
      s += "\n    if (debug) debug(\"" + m.getName() + "(\"";
      for (int i=0, N=pTypes.length; i<N; i++) {
      	s += " + f_or_mat(arg)" + i;
      	if (i<N-1) s += "+\",\"";
      }
      s += " + \")\"";
      if (retValue != null) s += " + \" = \" + val";
      s += ");";
      out.println(s);
      if (retValue != null) {
      	out.println("    return val;");
      }

      out.println("  }");
      if (cls.isInterface()) {
      	s = "  ";
      	s += "protected " + getName(retType) + " " + newMethodName + "(" + argString + ") {";
      	if (retValue != null) s += " return " + retValue + ";";
      	s += "}";
      	out.println(s);
      }
    }
    out.println();
    out.println(heading("Debugging support"));
    out.println("  private boolean debug;");
    out.println("  void setDebug(boolean debug) {this.debug = debug;}");
    out.println("  " + newName + "() {this(false);}");
    out.println("  " + newName + "(boolean debug) {setDebug(debug);}");
    out.println();
    out.println("  private void debug(String s) {");
    out.println("    System.err.println(prefix() + s);");
    out.println("  }");
    out.println("  private String prefix;");
    out.println("  private String prefix() {");
    out.println("    if (prefix == null) {");
    out.println("      String s = getClass().getName();");
    out.println("      int ilast = s.lastIndexOf('.');");
    out.println("      prefix = \"[\" + (ilast==-1 ? s : s.substring(ilast+1)) + \"] \";");
    out.println("    }");
    out.println("    return prefix;");
    out.println("  }");  
    out.println("  private String f_or_mat(Object o) {");
    out.println("	   if (o == null) return null;");
    out.println("	   String s = String.valueOf(o);");
    out.println("	   return s.length() > 100 ? o.getClass().getName() : s;");	
    out.println("  }");    
    out.println("}");    
  }
  
  private Object heading(String s) {
  	//
  	// This is one of the grosser pieces of code I've written in some time
  	//
  	String line = "  // -----------------------------------------------------------";
  	String str  = "  // ";
  	while (str.length() < (line.length()-1-s.length())/2) str += "-";
  	str += " " + s + " ";
  	while (str.length() < line.length()) str += "-";
  	return line + "\n" + str + "\n" + line + "\n";
	}

	private String getName(Class cls) {
  	if (cls.isArray()) {
  		Class c = cls.getComponentType();
  		String s = getImportedName(cls);
  		String result = c.getName();
  		for (int i=0; i<s.length(); i++) {
  			if (s.charAt(i) == '[') result += "[]";
  		}
  		return result;
  	} else {
  		return getImportedName(cls);
  	}
  }
  
  private String getImportedName(Class c) {
  	String s = c.getName();
  	int ilastDot = s.lastIndexOf('.');
  	if (ilastDot != -1) {
  		String pkg = s.substring(0,ilastDot);
  		if (pkg.equals("java.lang")) return s.substring(ilastDot+1);
  	}
  	return s;
  }

  private Object returnValue(Class c) {
    if (c == void.class) return null;
    if (c == int.class) return new Integer(0);
    if (c == short.class) return new Short((short)0);
    if (c == float.class) return new Float(0.0);
    if (c == double.class) return new Double(0.0);
    if (c == long.class) return new Long(0);
    if (c == boolean.class) return Boolean.FALSE;
    if (c == char.class) return new Character((char)0);
    return "null";
  }

  private String simpleName(Class c) {
    String s = c.getName();
    int ilast = s.lastIndexOf('.');
    return ilast == -1 ? s : s.substring(ilast+1);
  }

  private List getMethods(Class cls) throws Exception {
    List lst = new ArrayList();
    getMethods(cls,lst);
    return lst;
  }

  @SuppressWarnings("unchecked")
private void getMethods(Class cls, List lst) throws Exception {
    if (cls == null) return;
    Method[] ms = cls.getMethods();
    for (int i=0; i<ms.length; i++) {
      Method m = ms[i];
      if (cls.isInterface()) {
      	if (Modifier.isAbstract(m.getModifiers())) lst.add(m);
      } else {
      	if (m.getDeclaringClass().equals(cls)) {
      		lst.add(m);
      	}
      }
    }
    //getMethods(cls.getSuperclass(),lst);
    //Class[] ifs = cls.getInterfaces();
    //for (int i=0; i<ifs.length; i++) getMethods(ifs[i],lst);
  }

}



