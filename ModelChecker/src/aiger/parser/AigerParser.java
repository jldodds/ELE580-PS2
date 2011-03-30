package aiger.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import aiger.model.AigerFile;
import aiger.model.AndGate;
import aiger.model.Expression;
import aiger.model.Latch;
import aiger.model.Not;
import aiger.model.Variable;

public class AigerParser {
	private int m;
	private int i;
	private int l;
	private int o;
	private int a;
	
	private Map<Integer, Integer> unresolvedLatches = new HashMap<Integer, Integer>();
	private Map<String, Expression> symbols = new HashMap<String, Expression>(); 
	private List<Integer> unresolvedOutputs = new LinkedList<Integer>();
	private Map<Integer, List<Integer>> unresolvedAnds = new HashMap<Integer, List<Integer>>();
	
	private void readHeader(String header){
		Scanner s= new Scanner(header);
		s.next(); // throw out aag
		m=s.nextInt();
		i=s.nextInt();
		l=s.nextInt();
		o=s.nextInt();
		a=s.nextInt();
	}
	
	private Variable readInput(String var){
		Scanner s = new Scanner(var);
		int id=s.nextInt();
		id=id/2;
		Variable v = new Variable(String.valueOf(id));
		symbols.put(v.getTheVariableName(), v);
		return v;
	}
	
	private Latch readLatch(String latch){
		Scanner s = new Scanner(latch);
		int id = s.nextInt()/2;
		int nextID = s.nextInt();
		unresolvedLatches.put(id, nextID);
		Latch l = new Latch(String.valueOf(id));
		symbols.put(String.valueOf(id), l);
		return l;
	}
	
	private void readOutput(String output){
		Scanner s = new Scanner(output);
		unresolvedOutputs.add(s.nextInt());
	}
	
	public AigerFile parseFile(String file){
		Scanner lineScanner = new Scanner(file);
		lineScanner.nextLine();//get to the first line
		readHeader(lineScanner.nextLine());
		
		for(int c=0; c<i; c++){
			readInput(lineScanner.nextLine());
		}
		
		for(int c=0; c<l; c++){
			readLatch(lineScanner.nextLine());
		}
		
		for(int c=0; c<o; c++){
			readOutput(lineScanner.nextLine());
		}
		
		for(int c=0; c<a; c++){
			readAndGate(lineScanner.nextLine());
		}
		
		Set<Expression> outputs = link();
		
		return new AigerFile(outputs, symbols);
	}
	
	private Expression getSymbol(int n){
		if(n%2==0){
			return symbols.get(n/2);
		}
		else{
			return new Not(symbols.get((n-1)/2));
		}
	}
	
	private void resolveAnds(){
		for(Integer i : unresolvedAnds.keySet()){
			List<Integer> l = unresolvedAnds.get(i);
			int lhs = l.get(0);
			int rhs = l.get(1);
			AndGate ag = (AndGate)symbols.get(String.valueOf(i));
			ag.setTheLeftInput(getSymbol(lhs));
			ag.setTheRightInput(getSymbol(rhs));
		}
	}
	
	private void resolveLatches(){
		for(Integer i: unresolvedLatches.keySet()){
			int expID = unresolvedLatches.get(i);
			Latch l = (Latch)symbols.get(String.valueOf(i));
			l.setTheNextState(getSymbol(expID));
		}
	}
	
	private Set<Expression> resolveOutputs(){
		Set<Expression> ret = new HashSet<Expression>();
		for(Integer i: unresolvedOutputs){
			ret.add(getSymbol(i));
		}
		return ret;
	}
	
	private Set<Expression> link(){
			resolveAnds();
			resolveLatches();
			return resolveOutputs();
	}
	
	private void readAndGate(String andGate) {
		Scanner s = new Scanner(andGate);
		int id = s.nextInt()/2;
		int lhsID = s.nextInt();
		int rhsID = s.nextInt();
		List<Integer> lis = new LinkedList<Integer>();
		lis.add(lhsID);
		lis.add(rhsID);
		unresolvedAnds.put(id, lis);
		AndGate ag = new AndGate(String.valueOf(id));
		symbols.put(String.valueOf(id), ag);
	}
}
