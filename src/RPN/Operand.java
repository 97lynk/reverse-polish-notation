package RPN;

public class Operand {
	
	private String value;


	public Operand(String value) {
		this.value = value;
	}

	public String getValue() {
		
		return value;
	}

	public void setValue(String value) {
		
		this.value = value;
	}

	//Phép toán 2 ngôi
	public static String evaluate(Operand o1, Operand o2, String operator) {
		
		double a,b;
		try {
			
			a = Double.parseDouble(o1.getValue());
			b = Double.parseDouble(o2.getValue());
		}catch(Exception e) {return "NaN";}
		
		double result = 0;
		switch (operator) {
		
			case "+": result = a+b; break;
			case "-": result = a-b;	break;
			case "*": result = a*b;	break;
			case "/": result = a/b;	break;
			case "^": result = Math.pow(a,b); break;
		}
        return Double.toString(result);
     }

	//Phép toán 1 ngôi
	public static String evaluate(Operand o, String operator) {

		double a;
		try {

			a = Double.parseDouble(o.getValue());
		}catch(Exception e) {return "NaN";}
		
		double result = 0;
		switch (operator) {
		
			case "abs": result = Math.abs(a); break;
			case "sqrt": result = Math.sqrt(a); break;
			case "sin": result = Math.sin(a);	break;
			case "cos": result = Math.cos(a);	break;
			case "tan": result = Math.tan(a); break;
			case "ln": result = Math.log(a); break;
			case "log": result = Math.log10(a); break;
			case "exp": result = Math.exp(a); break;
		}
        return Double.toString(result);
     }
}
