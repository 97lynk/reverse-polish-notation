package RPN;

import java.util.LinkedList;

public class InFix {
	
	private String str;
	private LinkedList<String> tokens;

	public String getStr() {
		
		return str;
	}

	public void setStr(String str) {
		
		this.str = infixToStandard(str);
		this.tokens = infixToTokens(this.str);
	}

	public LinkedList<String> getTokens() {
		
		return tokens;
	}
	
	public InFix() {}
	public InFix(String str) {
		
		this.str = infixToStandard(str);
		this.tokens = infixToTokens(this.str);
	}
	public InFix(PostFix postfix) {
		
		String Str = "";
		for(int i=0; i<postfixToInfix(postfix).size(); i++)
			Str += postfixToInfix(postfix).get(i);
		this.str = Str;
		this.tokens = postfixToInfix(postfix);
	}
	//Chuẩn hóa chuỗi
	private String infixToStandard(String str) {
		
		str = str.replace(" ", "");
		for(int i=0; i<str.length(); i++) {
			
			str = str.replace("++", "+");
			str = str.replace("+-", "-");
			str = str.replace("-+", "-");
			str = str.replace("--", "+");
			str = str.replace("(+", "(");
			str = str.replace("-(", "(-1)*(");
			str = str.replace("(-abs", "((-1)*abs");
			str = str.replace("(-sqrt", "((-1)*sqrt");
			str = str.replace("(-sin", "((-1)*sin");
			str = str.replace("(-cos", "((-1)*cos");
			str = str.replace("(-tan", "((-1)*tan");
			str = str.replace("(-ln", "((-1)*ln");
			str = str.replace("(-log", "((-1)*log");
			str = str.replace("(-exp", "((-1)*exp");
		}
		if(str.charAt(0)=='+')
			str = str.substring(1);
		else if(str.charAt(0)=='-')
			if(	str.length()>3 && str.substring(1, 4).equals("abs") ||
				str.length()>4 && str.substring(1, 5).equals("sqrt") ||
				str.length()>3 && str.substring(1, 4).equals("sin") ||
				str.length()>3 && str.substring(1, 4).equals("cos") ||
				str.length()>3 && str.substring(1, 4).equals("tan") ||
				str.length()>2 && str.substring(1, 3).equals("ln") ||
				str.length()>3 && str.substring(1, 4).equals("log") ||
				str.length()>3 && str.substring(1, 4).equals("exp") )
				str = "(-1)*" + str.substring(1);
		return str;
	}
	//Kiểm tra kí tự toán tử
	private boolean isOperator(String s) {
    	
        return precedence(s) > 0;
    }
    //Xét độ ưu tiên toán tử
    private int precedence(String s) {

        if (s.equals("(") || s.equals(")")) return 1;
        else if (s.equals("+") || s.equals("-")) return 2;
        else if (s.equals("*") || s.equals("/") || s.equals("^")) return 3;
        else if (s.equals("abs") || 
        		s.equals("sqrt") || 
        		s.equals("sin") || s.equals("cos") || s.equals("tan") || 
        		s.equals("ln") || s.equals("log") ||
        		s.equals("exp")) return 4;
        else return 0;
    }
    //Sử dụng LinkedList để lưu từng toán tử và toán hạng thành các token
	private LinkedList<String> infixToTokens(String str) {
		
		LinkedList<String> tokens = new LinkedList<String>();
		String tempStr = "";
		for(int i=0; i<str.length(); i++) {

			//Kiểm tra phát hiện toán tử 1 ngôi
			String s = "";
			if(i < str.length()-3 && str.substring(i, i+3).equals("abs")) {
				//i nhỏ hơn str.length()-3 vì chuỗi "abs" kiểm tra có 3 kí tự tránh lỗi từ str.substring()
				s += "abs";
				i=i+2;
			}
			else if(i < str.length()-4 && str.substring(i, i+4).equals("sqrt")) {
				
				s += "sqrt";
				i=i+3;
			}
			else if(i < str.length()-3 && str.substring(i, i+3).equals("sin")) {
				
				s += "sin";
				i=i+2;
			}
			else if(i < str.length()-3 && str.substring(i, i+3).equals("cos")) {
				
				s += "cos";
				i=i+2;
			}
			else if(i < str.length()-3 && str.substring(i, i+3).equals("tan")) {
				
				s += "tan";
				i=i+2;
			}
			else if(i < str.length()-2 && str.substring(i, i+2).equals("ln")) {
				
				s += "ln";
				i=i+1;
			}
			else if(i < str.length()-3 && str.substring(i, i+3).equals("log")) {
				
				s += "log";
				i=i+2;
			}
			else if(i < str.length()-3 && str.substring(i, i+3).equals("exp")) {
				
				s += "exp";
				i=i+2;
			}
			else s += str.charAt(i);
			//Kiểm tra nếu là toán hạng thì thêm vào chuỗi tạm thời
			if(!isOperator(s) || s.equals("-") && i==0 || 
					s.equals("-") && str.charAt(i-1)=='(' || 
					s.equals("-") && str.charAt(i-1)=='*' || 
					s.equals("-") && str.charAt(i-1)=='/') {
				
				tempStr += s;
			}
			//Nếu là toán tử thì
			else {
				//Kiểm tra chuỗi tạm thời tồn tại toán hạng thì thêm vào List
				if(!tempStr.isEmpty()) {
					
					tokens.add(tempStr);
				}
				tempStr = "";
				//Thêm toán tử vào List
				tokens.add(s);
			}
			//Thêm toán hạng cuối vào List
			if(i==str.length()-1)
				if (!tempStr.isEmpty())
					tokens.add(tempStr);
		}
		return tokens;
	}
	//Sử dụng LinkedList để lưu infix sau khi chuyển từ postfix
	private LinkedList<String> postfixToInfix(PostFix postfix) {
		
    	LinkedList<String> infix = new LinkedList<String>();
    	String tempStr = "";
    	for (int i = 0; i < postfix.getTokens().size(); i++) {
    		
    		tempStr = postfix.getTokens().get(i);
			//Nếu là toán hạng thì thêm vào List
			if (precedence(tempStr) == 0) {
				
				infix.add(tempStr);
			}
			//Trường hợp toán tử 1 ngôi
			else if (precedence(tempStr) == 4) {
				
				String b ="" + infix.getLast();	infix.removeLast();
				infix.add(tempStr + "(" + b + ")");
			} 
			//Trường hợp toán tử 2 ngôi
    		else {
    			
				String b ="" + infix.getLast();	infix.removeLast();
				String a ="" + infix.getLast();	infix.removeLast();
				infix.add("(" + a + tempStr + b + ")");
    		}
			tempStr = "";
		}
    	return infix;
    }
}
