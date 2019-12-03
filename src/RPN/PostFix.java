package RPN;

import javafx.geometry.Pos;

import java.util.LinkedList;
import java.util.Stack;

public class PostFix {
	
	private String str;
	private LinkedList<String> tokens;
	private String result;

	public String getStr() {
		
		return str;
	}
	
	public void setStr(String str) {
		
		this.str = str;
		this.tokens = postfixToTokens(str);
		this.result = result();
	}

	public LinkedList<String> getTokens() {
		
		return tokens;
	}
	
	public String getResult() {
		
		return result;
	}
	
	public PostFix() {}

	public PostFix(String str) {
		
		this.str = str;
		this.tokens = postfixToTokens(str);
		this.result = result();
	}
	public PostFix(InFix infix) {
		
		String Str = "";
		for(int i=0; i<infixToPostfix(infix).size(); i++)
			Str += infixToPostfix(infix).get(i) + " ";
		this.str = Str;
		this.tokens =  infixToPostfix(infix);
		this.result = result();
	}

	//Kiểm tra kí tự toán tử
	public static boolean isOperator(String s) {
        return PostFix.precedence(s) > 0;
    }

	//Xét độ ưu tiên toán tử
    public static int precedence(String s) {

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
    private LinkedList<String> postfixToTokens(String str) {
		
		LinkedList<String> tokens = new LinkedList<String>();
		String tempStr = "";
		//Các token postfix cách nhau bởi khoảng trắng
		for(int i=0; i<str.length(); i++) {
			
			char c = str.charAt(i);
			
			if (c != ' ') {
				
				tempStr += c;
			}
			else {
				
				if(!tempStr.isEmpty()) {
					
					tokens.add(tempStr);
				}
				tempStr = "";
			}
			if(i==str.length()-1)
				if (!tempStr.isEmpty())
					tokens.add(tempStr);
		}
		return tokens;
	}

	//Sử dụng LinkedList để lưu postfix sau khi chuyển từ infix
	private LinkedList<String> infixToPostfix(InFix infix) {
		
		LinkedList<String> postfix = new LinkedList<>();
        Stack<String> operator = new Stack<>();
        String popped;

        for (int i = 0; i < infix.getTokens().size(); i++) {

        	String s = infix.getTokens().get(i);
            //Nếu là toán hạng thì thêm vào cuối postfix
            if (!isOperator(s))
                postfix.add(s);
            //Nếu là dấu đóng ngoặc “)”: 
            //lấy các toán tử trong stack ra và cho vào cuối postfix cho đến khi gặp dấu mở ngoặc “(“ 
            //(Dấu đóng ngoặc cũng phải được đưa ra khỏi stack)
            else if (s.equals(")"))
                while (!(popped = operator.pop()).equals("("))
                	postfix.add(popped);
            //Nếu là toán tử:
            //-Chừng nào ở đỉnh stack là toán tử đó có độ ưu tiên lớn hơn toán tử hiện tại thì 
            //lấy toán tử đó ra khỏi stack và cho ra postfix
            //-Đưa toán tử hiện tại vào stack
            else {
                while (!operator.isEmpty() && !s.equals("(") && precedence(operator.peek()) >= precedence(s))
                	postfix.add(operator.pop());
                operator.push(s);
            }
        }
        //Nếu trong stack còn toán tử thì lấy các token trong đó ra và cho lần lượt vào postfix
        while (!operator.isEmpty())
        	postfix.add(operator.pop());

        return postfix;
	}
	
	private String result() {
		
    	Stack<String> result = new Stack<>();
    	for (int i = 0; i < tokens.size(); i++) {
			
    		String s = tokens.get(i);
			//Nếu toán tử là phép toán 2 ngôi
    		if (precedence(s) == 2 || precedence(s) == 3) {
    			
    			if(result.size()<2) break;
    			Operand b = new Operand(result.pop());
    			Operand a = new Operand(result.pop());
    			result.add(Operand.evaluate(a, b, s));
    		}
    		//Nếu toán tử là phép toán 1 ngôi
    		else if (precedence(s) == 4) {
    			
    			if(result.size()<1) break;
    			Operand b = new Operand(result.pop());
    			result.add(Operand.evaluate(b, s));
    		}
    		else result.add(s);
    	}
    	try {
    		
        	return result.pop();
    	}catch(Exception e) {return "NaN";}
    }
}
