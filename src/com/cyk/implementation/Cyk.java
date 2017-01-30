package com.cyk.implementation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Implements the CYK Algorithm that uses a context-free grammar file and checks
 * to see whether a given string is accepted by that grammar.
 * 
 */
public class Cyk
{
   // Constants
   public static final int GRAMMAR_FILE = 0;
   public static final int FILE_ERROR = 1;
   public static final int LAST_ARG = 1;
   public static final int PARSE_ERROR = 2;
   public static final int TOTAL_ARGS = 2;

   /* The 2 dimensional table for the CYK algorithm */
   private static ArrayList<String>[][] table;

   private HashMap<String, List<String[]>> variables;

   private HashMap<String, List<String>> terminals;

   /* The start variable */
   private static String startVariable;
   
   Map<Integer,String> derivations = new HashMap<Integer,String>();
   /**
    * Constructs a Cyk object and initializes the HashMaps of the variables
    * and the terminals
    */
   public Cyk()
   {
      variables = new HashMap<String, List<String[]>>();
      terminals = new HashMap<String, List<String>>();
      //terminals = new HashMap<String, Character>();
   }

   /**
    * Processes the grammar file and builds the HashMap of the list of terminals
    * and variables. Uses the Scanner object to read the grammar file.
    * @param file the string representing the path of the grammar file
    */
  
   public void processGrammarFile(String file)
   {
      File grammarFile = null;
      Scanner scanner = null;
      try
      {
         grammarFile = new File(file);
         scanner = new Scanner(grammarFile);
         String[] line = scanner.nextLine().split("->");
         startVariable = line[0];
         do
         {
            String variable = line[0];
            if(line[1].contains("'"))
            {
            	List<String> terminalArr;
            	String terminal = line[1].replace("'", "");
            	if(terminals.containsKey(variable)){
            		terminalArr = terminals.get(variable);
            		terminalArr.add(terminal.trim());
            		terminals.put(variable, terminalArr);
            	}else{
            		terminalArr = new ArrayList<String>();
            		terminalArr.add(terminal.trim());
            		terminals.put(variable, terminalArr);
            	}
            	
            }
            else
            {
               String[] rest = line[1].trim().split(" ");
               if (rest != null && rest.length<=2)
               {
            	  if(variables.containsKey(variable)){
            		  List<String[]> lstDerivation = variables.get(variable);
            		  lstDerivation.add(rest);
             		  variables.put(variable, lstDerivation);
            	  }else{
            		 List<String[]> lstDerivation = new ArrayList<String[]>();  
            		 lstDerivation.add(rest);
            		 variables.put(variable, lstDerivation);
            	  }
                 
               }else{
            	  System.out.println("Ignoring as its not valid : "+line[0]+"->"+line[1]);
               }
            }
            if (scanner.hasNextLine())
               line = scanner.nextLine().split("->");
            else
               line = null;
         } while (line != null);
         scanner.close();
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }
   }
   /**
    * Tests the string against the given grammar file using the CYK Algorithm.
    * In the current version, warnings about type safety, are suppressed
    * @param w the input string to test
    * @return true if string w is accepted by the grammar, false otherwise.
    */
   @SuppressWarnings("unchecked")
   public boolean processString(String[] w)
   {
	  
	  StringBuffer outStr = new StringBuffer();
	  int length = w.length;
      table = new ArrayList[length][];
      for (int i = 0; i < length; ++i)
      {
         table[i] = new ArrayList[length];
         for (int j = 0; j < length; ++j)
            table[i][j] = new ArrayList < String > ();
      } 
      for (int i = 0; i < length; ++i)
      {
         Set<String> keys = terminals.keySet();
         
         for (String key : keys)
         {
            if (terminals.get(key).contains(w[i]))
               table[i][i].add(key.trim());   	
         }
         //System.out.println(table[i][i]);
      }
      int countDerivative=0;
      for (int l = 2; l <= length; ++l)
      {
    	  
         for (int i = 0; i <= length - l; ++i)
         {
            int j = i + l - 1;
            for (int k = i; k <= j - 1; ++k)
            {
               //System.out.println("i: "+i+" j : "+j+" k : "+k);
               Set<String> keys = variables.keySet();
               //System.out.println(table[i][k] + " :::: "+ table[k + 1][j]);
               for (String key : keys)
               {
            	  List<String[]> listDerivatives = variables.get(key);
                  for(String[] values:listDerivatives){
                	  //System.out.println("key : "+ key + " value: "+ values[0]  + " "+  values[1] );
                	  if (table[i][k].contains((values[0]))
                              && table[k + 1][j].contains(values[1])){
                		  countDerivative++;
                		  //System.out.println("Add at  table["+i+"]["+ j+"] : "+key);
                		  derivations.put(countDerivative, key.trim()+"->"+values[0]+" "+values[1]);
                		  table[i][j].add(key.trim());
                		  
                	  }
                           
                  }
            	  
               }
            }
         }
      }
      
      int i =0;
      int j =0;
      
      /*String previousRH[]=null;
      int size = derivations.size();
      for(int p = size;p>0;p--){
    	  //System.out.println(derivations.get(p));
    	  String derivative = derivations.get(p);
    	  String sides[]= derivative.split("->");
    	  if(p == size && sides[0].equals(startVariable.trim())){
    		  System.out.println(sides[0]);
    		  System.out.println(sides[1]);
        	  previousRH = sides[1].split(" ");
    	  }
    	  else if (p != size && sides[0].equals(startVariable)) {
			continue;
    	  }
    	  if(p!=size){
    	  if(previousRH[0].equals(sides[0])){
    		  System.out.println(sides[1]+" "+previousRH[1]);
    	  }else if (previousRH[1].equals(sides[0])) {
    		  System.out.println(previousRH[0]+" "+sides[1]);
    	  }
    	  }
    	  
    	  
      }*/
      
      
      for (int l = 1; l <= length; ++l)
      {
    	 String spaces = String.format("%"+l*2+"s", "");
    	 System.out.println();
    	 System.out.print(spaces);
    	 while(i<length && j<length){
    		 if(table[i][j].isEmpty()){
    			 System.out.print("[-] ");
    		 }else{
    			 System.out.print(table[i][j]+" ");
    		 }
    		
    		 i++;
    		 j++;
    	 }
    	 i=0;
    	 j= i+l;
    	 //System.out.println();
      }
      
      
      if (table[0][length - 1].contains(startVariable.trim())) // we started from 0
         return true;
      return false;
   }

   /**
    * Takes a given grammar file as the input and a given string to test
    * against that grammar.
    * @param args the list of the given command-line arguments consisting
    *             of the grammar file and the string to test, strictly in that
    *             order.
    */
   public static void main(String[] args)
   {
	  if(args.length==0){
		  args = new String[2];
		  args[0] = "grammer-demo.txt";
		  args[1] = "I shot an elephant in my pajamas";
	  }
	  
      if (args.length != TOTAL_ARGS)
      {
         System.out.println("Usage: java Cyk grammar_file some_string");
         System.exit(FILE_ERROR);
      }

      Cyk c = new Cyk();
      c.processGrammarFile(args[GRAMMAR_FILE]);
      if (c.processString(args[LAST_ARG].split(" ")))
         System.out.println("\n"+"true : String matches");
      else
         System.out.println("\n"+"false : String doesn't match");
   }
}