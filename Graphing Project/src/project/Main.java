package project;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class Main {

	public static void main(String[] args) {
		
		Scanner reader = new Scanner(System.in);
		System.out.println("Enter your equation (use x as your variable):");
		String e = reader.nextLine();
		Expression equation = new ExpressionBuilder(e).variables("x").build();
		graph(equation);
		// Workaround due to issues with derivative precision mentioned below
		System.out.println("Enter your equation's derivative (use x as your variable):");
		String d = reader.nextLine();
		Expression derivative = new ExpressionBuilder(d).variables("x").build();
		
		
		System.out.println("Enter min value in search range:");
		double min = reader.nextDouble();
		System.out.println("Enter max value in search range:");
		double max = reader.nextDouble();
		double guess = Guess(min, max, equation, derivative);
		if (guess == min - 1) {
			//System.out.println("Range is invalid")
		}
		//System.out.println("Guess = " +guess);
		
		
		/*//: This is my workaround due to the aforementioned issues
		System.out.println("Enter your guess for the zero:");
		double guess = reader.nextDouble();
		reader.nextLine();*/
		
		
		//: Now the actual newton's method loop
		while(true) {
			double xy[] = {guess, equation.setVariable("x", guess).evaluate()};
			double solution = FindZero(xy, FindDerivative(guess, derivative));
			if (equals(solution, guess)) {
				break;
			}else {
				//some test features
				//System.out.println(solution +" "+guess);
				//System.out.println(test.toPlainString());
				guess = solution;
				}
			}
		BigDecimal x = new BigDecimal(Math.abs(guess), MathContext.DECIMAL32);
		if(x.toPlainString().length() > 8) {
			System.out.println("{"+x.toPlainString().substring(0, 8)+","+"0}");
		}else{
			System.out.println("{"+x.toPlainString()+","+"0}");
			}
		}

	private static double Guess (double min, double max, Expression e, Expression derivative) {
		double guess[] = {0, Double.MAX_VALUE};
		int cntSignChange = 0;
		boolean isPositive = true;
		if (e.setVariable("x", min).evaluate() < 0) {
			isPositive = false;
		}
		for (double cnt = min; cnt <= max; cnt += 0.00001) {
			e.setVariable("x", cnt);
			if(guess[1] > Math.abs(e.evaluate())) {
				guess[0] = cnt;
			}
			if(equals(FindDerivative(cnt,derivative),0.0000000000000000000000000000000000000000, 4)) {
				e.setVariable("x", cnt);
				if (equals(e.evaluate(),0.00000000000000000000000000000000000000000000, 4)) {
					return cnt+0.01;
				}
			}
				
			if((e.evaluate() >= 0) != isPositive) {
				cntSignChange++;
				if(isPositive == true) {
					isPositive = false;
				}
				else {
					isPositive = true;
				}
			}
		}
		if (cntSignChange != 1) {
			guess[0] = min - 1;
		}
	
		return guess[0];
	}
		
	private static double FindDerivative (double value, Expression e) {
		//: this doesn't work, because of issues with the double format in java and it being not exact enough 
		/*MathContext mc = new MathContext(16);
		BigDecimal test1 = new BigDecimal(e.setVariable("x", value + 0.000000000000001).evaluate(), mc);
		BigDecimal test2 = new BigDecimal(e.setVariable("x", value - 0.000000000000001).evaluate(), mc);
		System.out.println(test1);
		System.out.println(test2);
		System.out.println(test1.subtract(test2));
		double derivative = test1.subtract(test2).divide(BigDecimal.valueOf(0.000000000000002)).doubleValue();
		return derivative;
		*/
		//:this is my work around using a pre - entered derivative
		e.setVariable("x", value);
		double derivative = e.evaluate();
		return derivative;
	}
	
	private static double FindZero(double[] xy, double slope) {
		double solution = -xy[1];
		solution += slope*xy[0];
		solution /= slope;
		return solution;
	}
	
	private static boolean equals(double number1, double number2) {
		BigDecimal number1Decimal = new BigDecimal(Math.abs(number1), MathContext.DECIMAL32);
		BigDecimal number2Decimal = new BigDecimal(Math.abs(number2), MathContext.DECIMAL32);
		if (number1Decimal.toPlainString().regionMatches(0, number2Decimal.toPlainString(), 0, 9)||number1Decimal.equals(number2Decimal)) {
			return true;
		}else {
			return false;
		}
	}
	
	private static boolean equals(double number1, double number2, int confidence) {
		BigDecimal number1Decimal = new BigDecimal(Math.abs(number1), MathContext.DECIMAL32);
		BigDecimal number2Decimal = new BigDecimal(Math.abs(number2), MathContext.DECIMAL32);
		if (number1Decimal.toPlainString().regionMatches(0, number2Decimal.toPlainString(), 0, confidence)||number1Decimal.equals(number2Decimal)) {
			return true;
		}else {
			return false;
		}
	}
	private static void graph(Expression equation){	
		Plot plot = new Plot(equation);
		plot.setLayout(null);
		JFrame frame = new JFrame();
		frame.setBackground(Color.white);
		frame.getContentPane().add(plot);
		plot.updateUI();
		  frame.setSize(800,800);
		frame.setVisible(true);
		}
	}


	class Plot extends JPanel
	{
	    	double[] x = null;
	        double[] y = null;
	        int size = 2001;
	        int[] xx = new int[size];
	        int[] yy = new int[size];
	        String xlabel, ylabel, title;
	        int xdim, ydim, yzero, xzero, xdraw, ydraw;
	        double xtic, ytic, xpoint, ypoint;
	        double xmax, xmin, ymax, ymin; 
	 
	public Plot(Expression equation)
	{
	 
	        double[][]coordinates = points(equation);
	        x = coordinates[0];
	        y = coordinates[1];
	        size = x.length;
			xdim = 600;
	        ydim = 600;
	            

	 
	        xmax = x[0];
	                xmin = x[0];
	                ymax = y[0];
	                ymin = y[0];
	                 
	                for (int i=0; i < size; i++){
	                if (x[i] > xmax) {
	                xmax = x[i];
	                }
	                        if (x[i] < xmin) {
	                        xmin = x[i];
	                        }
	                        if (y[i] > ymax) {
	                        ymax = y[i];
	                        }
	                        if (y[i] < ymin) {
	                        ymin = y[i];
	                        }
	                         
	                }
	             xtic = 5;
		         ytic = ymax/10;
	         
	        //xx and yy are the scaled x and y used for plotting
	                                 
	                for (int i=0; i < size; i++){
	               xx[i] = (int) (50 + (((x[i]-xmin)/(xmax-xmin)) * (xdim-100)));
	                yy[i] = (int) ((ydim - 50) - (((y[i]-ymin)/(ymax-ymin)) * (ydim-100)));
	                }
	                 
	//Find Zero point on y-axis required for drawing the axes
	                 
	                if ((ymax*ymin) < 0){
	                yzero = (int) ((ydim - 50) - (((0-ymin)/(ymax-ymin)) * (ydim-100)));
	                }
	                else{
	                yzero = (int) ((ydim - 50) - ((0/(ymax-ymin)) * (ydim-100)));
	                }
	                 
	//Find zero point on x-axis required for drawing the axes
	                 
	                if ((xmax*xmin) < 0) {
	                xzero = (int) (50 + (((0-xmin)/(xmax-xmin)) * (xdim-100)));
	                }
	                else{
	                xzero = (int) (50 + ((0/(xmax-xmin)) * (xdim-100)));
	                }
	                 
	//Now ready to plot the results
	                repaint();     
	                 
	               
	   
	  }
	   
	   
	  public void paint(Graphics g){   
	                 
	                Font f1 = new Font("TimesRoman", Font.PLAIN, 10);
	                g.setFont(f1);
	                 
	//First draw the axes
	                  
	//y-axis
	                 
	                g.drawLine(xzero, 50, xzero, ydim-50);

	                                 
	//x-axis
	                 
	                g.drawLine(50, yzero, xdim-50, yzero);

	                 
	//Initialise the labelling taking into account the xtic and ytic values
	                                 
	                //x-axis labels
	                 
	                if (xmin <= 0){
	                xpoint = xmin - (xmin%xtic);
	                }else{
	                        xpoint = xmin - (xmin%xtic) + xtic;
	                }
	                 
	                do{
	                xdraw = (int) (50 + (((xpoint-xmin)/(xmax-xmin))*(xdim-100)));
	                g.drawString(xpoint + "", xdraw, (yzero+10));
	                xpoint = xpoint + xtic;
	                }while (xpoint <= xmax);
	                 
	                if (ymin <= 0){
	                ypoint = ymin - (ymin%ytic);
	                }else{
	                        ypoint = ymin - (ymin%ytic) + ytic;
	                }
	                 
	                do{
	                ydraw = (int) ((ydim - 50) - (((ypoint - ymin)/(ymax-ymin))*(ydim-100)));
	                g.drawString(ypoint + "", (xzero - 20), ydraw);
	                ypoint = ypoint + ytic;
	                }while (ypoint <= ymax);
	                 
	// Draw Lines
	                 
	                for (int j = 0; j < size-1; j++)
	                {
	                 
	                        g.drawLine(xx[j], yy[j], xx[j+1], yy[j+1]);  
	                }  
	                 
	                 
	        }
	  public double[][] points(Expression equation){
		  double[][] coordinates = new double[2][2001];
		  int counter = 0;
		  for (double cnt = -50; cnt <= 50; cnt+=0.05) {
			  coordinates[0][counter]=cnt;
			  coordinates[1][counter]=equation.setVariable("x", cnt).evaluate();
			  counter++;
		  }
		  return coordinates;
	  }
	}


