package com.lc.nlp4han.constituent.pcfg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 上下文无关文法
 * 
 * 包含：开始符，重写规则，非终结符集，终结符集
 */
public class CFG
{
	private String startSymbol = null;
	private Set<String> nonTerminalSet = new HashSet<String>();// 非终结符集
	private Set<String> terminalSet = new HashSet<String>();// 终结符集
	private Set<RewriteRule> ruleSet = new HashSet<RewriteRule>();// 规则集
	
	private HashMap<String, HashSet<RewriteRule>> LHS2Rules = new HashMap<String, HashSet<RewriteRule>>();// 以左部为key值的规则集map
	private HashMap<ArrayList<String>, HashSet<RewriteRule>> RHS2Rules = new HashMap<ArrayList<String>, HashSet<RewriteRule>>();// 以规则右部为key值的规则集map

	/**
	 * 构造函数,一步创建
	 */
	public CFG(String startSymbol, Set<String> nonTerminalSet, Set<String> terminalSet,
			HashMap<String, HashSet<RewriteRule>> ruleMapStartWithlhs,
			HashMap<ArrayList<String>, HashSet<RewriteRule>> ruleMapStartWithrhs)
	{
		this.startSymbol = startSymbol;
		this.nonTerminalSet = nonTerminalSet;
		this.terminalSet = terminalSet;
		
		this.LHS2Rules = ruleMapStartWithlhs;
		this.RHS2Rules = ruleMapStartWithrhs;
		
		for (String lhs : ruleMapStartWithlhs.keySet())
		{
			ruleSet.addAll(ruleMapStartWithlhs.get(lhs));
		}
	}

	/**
	 * 构造函数，通过起始符，终结符集，非终结符集，规则集一步构造
	 * 
	 * @param startSymbol
	 * @param nonTerminalSet
	 * @param terminalSet
	 * @param rules
	 */
	public CFG(String startSymbol, Set<String> nonTerminalSet, Set<String> terminalSet, HashSet<RewriteRule> rules)
	{
		this.startSymbol = startSymbol;
		this.nonTerminalSet = nonTerminalSet;
		this.terminalSet = terminalSet;
		for (RewriteRule rule : rules)
		{
			add(rule);
		}
	}

	/**
	 * 从流中加载CFG文法，此接口可以完成从资源流和文件流中获得CFG文法
	 * 
	 * @param in
	 * @param encoding
	 * @throws IOException
	 */
	public CFG(InputStream in, String encoding) throws IOException
	{
		readGrammar(in, encoding);
	}

	/**
	 * 从流中加载CFG/PCFG文法，此接口可以完成从资源流和文件流中获得CFG/PCFG文法
	 * 
	 * @param in
	 * @param encoding
	 * @throws IOException
	 */
	public void readGrammar(InputStream in, String encoding) throws IOException
	{
		BufferedReader buffer = new BufferedReader(new InputStreamReader(in, encoding));
		String str = buffer.readLine().trim();
		if (str.equals("--起始符--"))
		{
			setStartSymbol(buffer.readLine().trim());
		}
		
		buffer.readLine();
		str = buffer.readLine().trim();
		while (!str.equals("--终结符集--"))
		{
			addNonTerminal(str);
			str = buffer.readLine().trim();
		}
		
		str = buffer.readLine();
		while (!str.equals("--规则集--"))
		{
			addTerminal(str);
			str = buffer.readLine().trim();
		}
		
		str = buffer.readLine();
		while (str != null)
		{
			str = str.trim();		
			add(readRule(str));

			str = buffer.readLine();
		}
		
		buffer.close();
	}
	
	protected RewriteRule readRule(String ruleStr)
	{
		return new RewriteRule(ruleStr);
	}

	/**
	 * 通过一步一步添加rule来实现规则集，终结符/非终结符的更新
	 */
	public CFG()
	{

	}

	/**
	 * 判断是否为CNF
	 */
	public boolean isCNF()
	{
		boolean isCNF = true;
		for (RewriteRule rule : ruleSet)
		{
			ArrayList<String> list = rule.getRhs();
			if (list.size() >= 3)
			{
				isCNF = false;
				break;
			}
			
			if (list.size() == 2)
			{
				for (String string : list)
				{
					if (!nonTerminalSet.contains(string))
					{
						isCNF = false;
						break;
					}
				}
			}
			
			if (list.size() == 1)
			{
				if (nonTerminalSet.contains(list.get(0)))
				{
					isCNF = false;
					break;
				}
			}
		}
		
		return isCNF;
	}
	
	/**
	 * 判断文法是宽松CNF文法
	 * 
	 * 宽松CNF文法允许A->B
	 */
	public boolean isLooseCNF()
	{
		boolean isLooseCNF = true;
		for (RewriteRule rule : ruleSet)
		{
			ArrayList<String> list = rule.getRhs();
			if (list.size() >= 3)
			{
				isLooseCNF = false;
				break;
			}
			
			if (list.size() == 2)
			{
				for (String string : list)
				{
					if (!nonTerminalSet.contains(string))
					{
						isLooseCNF = false;
						break;
					}
				}
			}
		}
		
		return isLooseCNF;
	}
	
	public boolean isNoTerminal(String symbol)
	{
		return nonTerminalSet.contains(symbol);
	}
	
	public boolean isTerminal(String symbol)
	{
		return terminalSet.contains(symbol);
	}

	public String getStartSymbol()
	{
		return startSymbol;
	}

	public void setStartSymbol(String startSymbol)
	{
		this.startSymbol = startSymbol;
	}

	public Set<String> getNonTerminalSet()
	{
		return nonTerminalSet;
	}

	public void setNonTerminalSet(Set<String> nonTerminalSet)
	{
		this.nonTerminalSet = nonTerminalSet;
	}

	public Set<String> getTerminalSet()
	{
		return terminalSet;
	}

	public void setTerminalSet(Set<String> terminalSet)
	{
		this.terminalSet = terminalSet;
	}

	public HashMap<String, HashSet<RewriteRule>> getLHS2Rules()
	{
		return LHS2Rules;
	}

	public void setLHS2Rules(HashMap<String, HashSet<RewriteRule>> lHS2Rules)
	{
		LHS2Rules = lHS2Rules;
	}

	public HashMap<ArrayList<String>, HashSet<RewriteRule>> getRHS2Rules()
	{
		return RHS2Rules;
	}

	public void setRHS2Rules(HashMap<ArrayList<String>, HashSet<RewriteRule>> rHS2Rules)
	{
		RHS2Rules = rHS2Rules;
	}

	public void setRuleSet(Set<RewriteRule> ruleSet)
	{
		this.ruleSet = ruleSet;
	}

	/**
	 * 添加单个规则
	 */
	public void add(RewriteRule rule)
	{
		ruleSet.add(rule);
		if (LHS2Rules.get(rule.getLhs()) != null)
		{
			LHS2Rules.get(rule.getLhs()).add(rule);
		}
		else
		{
			HashSet<RewriteRule> set = new HashSet<RewriteRule>();
			set.add(rule);
			LHS2Rules.put(rule.getLhs(), set);
		}
		
		if (RHS2Rules.keySet().contains(rule.getRhs()))
		{
			RHS2Rules.get(rule.getRhs()).add(rule);
		}
		else
		{
			HashSet<RewriteRule> set = new HashSet<RewriteRule>();
			set.add(rule);
			RHS2Rules.put(rule.getRhs(), set);
		}
	}

	/**
	 * 得到规则集
	 */
	public Set<RewriteRule> getRuleSet()
	{
		return ruleSet;
	}

	/**
	 * 添加非终结符
	 */
	public void addNonTerminal(String nonTer)
	{
		nonTerminalSet.add(nonTer);
	}

	/**
	 * 添加终结符
	 * 
	 * @param terminal
	 *            终结符
	 */
	public void addTerminal(String terminal)
	{
		terminalSet.add(terminal);
	}

	/**
	 * 根据规则左部得到所有对应规则
	 * 
	 * @param lhs
	 *            左侧字符串
	 */
	public Set<RewriteRule> getRuleBylhs(String lhs)
	{
		return LHS2Rules.get(lhs);
	}

	/**
	 * 根据规则右部得到所有对应规则
	 * 
	 * @param rhsList
	 *            右部字符串列表
	 * @return 字符串集合
	 */
	public Set<RewriteRule> getRuleByrhs(ArrayList<String> rhsList)
	{
		return RHS2Rules.get(rhsList);
	}

	/**
	 * 根据规则右部得到所有对应规则
	 * 
	 * @param args
	 *            右部字符串
	 * @return 字符串集合
	 */
	public Set<RewriteRule> getRuleByrhs(String... args)
	{
		ArrayList<String> list = new ArrayList<String>();
		for (String string : args)
		{
			list.add(string);
		}
		
		return RHS2Rules.get(list);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nonTerminalSet == null) ? 0 : nonTerminalSet.hashCode());
		result = prime * result + ((ruleSet == null) ? 0 : ruleSet.hashCode());
		result = prime * result + ((startSymbol == null) ? 0 : startSymbol.hashCode());
		result = prime * result + ((terminalSet == null) ? 0 : terminalSet.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		CFG other = (CFG) obj;
		
		if (nonTerminalSet == null)
		{
			if (other.nonTerminalSet != null)
				return false;
		}
		else if (!nonTerminalSet.equals(other.nonTerminalSet))
			return false;
		
		if (ruleSet == null)
		{
			if (other.ruleSet != null)
				return false;
		}
		else if (!ruleSet.equals(other.ruleSet))
			return false;
		
		if (startSymbol == null)
		{
			if (other.startSymbol != null)
				return false;
		}
		else if (!startSymbol.equals(other.startSymbol))
			return false;
		
		if (terminalSet == null)
		{
			if (other.terminalSet != null)
				return false;
		}
		else if (!terminalSet.equals(other.terminalSet))
			return false;
		
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder stb = new StringBuilder();
		Iterator<String> itr1 = nonTerminalSet.iterator();
		stb.append("--起始符--" + '\n');
		stb.append(this.getStartSymbol() + '\n');

		stb.append("--非终结符集--" + '\n');
		while (itr1.hasNext())
		{
			stb.append(itr1.next() + '\n');
		}

		Iterator<String> itr2 = terminalSet.iterator();
		stb.append("--终结符集--" + '\n');
		while (itr2.hasNext())
		{
			stb.append(itr2.next() + '\n');
		}

		stb.append("--规则集--" + '\n');
		Set<String> set = LHS2Rules.keySet();
		for (String string : set)
		{
			HashSet<RewriteRule> ruleSet = LHS2Rules.get(string);
			Iterator<RewriteRule> itr3 = ruleSet.iterator();
			while (itr3.hasNext())
			{
				stb.append(itr3.next().toString() + '\n');
			}
		}
		return stb.toString();
	}
}