package proof.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChainClause extends Clause {
	public ArrayList<Clause> clauses;
	public ArrayList<Variable> pivots;
	Set<Literal> resolvents = null;
	
	public ChainClause() {
		this.clauses = new ArrayList<Clause>();
		this.pivots = new ArrayList<Variable>();
	}
	
	public ChainClause(ArrayList<Clause> resolution_clauses, ArrayList<Variable> pivots) {
		assert(resolution_clauses.size() == pivots.size()+1);
		this.clauses = resolution_clauses;
		this.pivots = pivots;
	}
	
	public boolean resolve()
	{	
		Iterator<Clause> it_clause= this.clauses.iterator(); 
		Iterator<Variable> it_pivots= this.pivots.iterator();
		
		Set<Literal> literals = it_clause.next().getLiterals();
		while(it_clause.hasNext())
		{
			literals = this.resolveLiterals(literals, it_clause.next().getLiterals(), it_pivots.next());
			if (literals == null)
			{
				return false;
			}
		}
		
		assert(!it_clause.hasNext() && !it_pivots.hasNext());
		this.resolvents = literals;
		return true;
	}
	
	public Set<Literal> resolveLiterals(Set <Literal> right, Set <Literal> left, Variable pivot)
	{
		Literal pos = new Literal(pivot, true);
		Literal neg = new Literal(pivot, false);
	    
		boolean correct = ((right.contains(pos) || left.contains(pos)) && (left.contains(neg) ||right.contains(neg))); 
		if(!correct)
		{
			return null;
		}
		
		//use HashSet to ensure uniqueness of literals
		HashSet<Literal> set = new HashSet<Literal>(right);
		set.addAll(left);
		set.remove(pos);
		set.remove(neg);
		
		return set;
	}
	
	@Override
	public Set<Literal> getLiterals()
	{
		if(this.resolvents == null)
		{
			this.resolve();
		}
		assert(this.resolvents != null);
		return this.resolvents;
	}

	@Override
	void print() {
		// TODO Auto-generated method stub
		System.out.println("Start of Chain Clause");
		Iterator<Clause> it_clause= this.clauses.iterator(); 
		Iterator<Variable> it_pivots= this.pivots.iterator();
		Iterator<Literal> it_literal;
		
		Set<Literal> literals = it_clause.next().getLiterals();
		while(it_clause.hasNext())
		{
			System.out.print("Right: ");
			it_literal = literals.iterator();
			while(it_literal.hasNext()) {System.out.print(" "+it_literal.next().toString());}
			System.out.print(" Left: ");
			it_literal = it_clause.next().getLiterals().iterator();
			while(it_literal.hasNext()) {System.out.print(" "+it_literal.next().toString());}
            System.out.print(" Pivot: "+it_pivots.next().toString());
            System.out.print("\n");
		}
		System.out.print("Final Literals: ");
		it_literal = this.getLiterals().iterator();
		while(it_literal.hasNext()) {System.out.print(" "+it_literal.next().toString());}
		System.out.println("\nEnd of Chain Clause");
	}
	
	public void traverse(ProofTraverser r)
	{
		r.chain(this);
	}
}
