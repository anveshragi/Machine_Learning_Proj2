import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BinaryAdaBoosting {

	private static String filename;
	private  int T;
	private int n;
	private double epsilon;
	private double training_set_x[];
	private double training_set_y[];
	private double training_set_p[][];		// At index 0 - initial probabilities, from 1 to T+1 - new probabilities
	private double threshold_array[];
	private double error_array[];
	private boolean equality_array[];		// True for representing <=, false for >
	private double goodness_weight[];
	private char correct_array[][];			// to check if h(x) == y or not after selecting classifier
	private StringBuilder boosted_classifier;
	private double bound;

	public BinaryAdaBoosting() {
		super();
		filename = "C:/Users/Anvesh/Google Drive/workspace/ML2/src/file.txt";
		this.T = 0;
		this.n = 0;
		this.epsilon = 0.0;
		this.bound = 1.0;
		this.boosted_classifier = new StringBuilder();
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		BinaryAdaBoosting.filename = filename;
	}

	public int getT() {
		return T;
	}

	public void setT(int t) {
		T = t;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public double[] getTraining_set_x() {
		return training_set_x;
	}

	public void setTraining_set_x(double[] training_set_x) {
		this.training_set_x = training_set_x;
	}

	public double[] getTraining_set_y() {
		return training_set_y;
	}

	public void setTraining_set_y(double[] training_set_y) {
		this.training_set_y = training_set_y;
	}

	public double[][] getTraining_set_p() {
		return training_set_p;
	}

	public void setTraining_set_p(double[][] training_set_p) {
		this.training_set_p = training_set_p;
	}

	public double[] getThreshold_array() {
		return threshold_array;
	}

	public void setThreshold_array(double[] threshold_array) {
		this.threshold_array = threshold_array;
	}

	public double[] getError_array() {
		return error_array;
	}

	public void setError_array(double[] error_array) {
		this.error_array = error_array;
	}

	public boolean[] getEquality_array() {
		return equality_array;
	}

	public void setEquality_array(boolean[] equality_array) {
		this.equality_array = equality_array;
	}

	public double[] getGoodness_weight() {
		return goodness_weight;
	}

	public void setGoodness_weight(double[] goodness_weight) {
		this.goodness_weight = goodness_weight;
	}

	public char[][] getCorrect_array() {
		return correct_array;
	}

	public void setCorrect_array(char[][] correct_array) {
		this.correct_array = correct_array;
	}

	public StringBuilder getBoosted_classifier() {
		return boosted_classifier;
	}

	public void setBoosted_classifier(StringBuilder boosted_classifier) {
		this.boosted_classifier = boosted_classifier;
	}

	public double getBound() {
		return bound;
	}

	public void setBound(double bound) {
		this.bound = bound;
	}

	
	
	
	public void readFile(String fname) {

		File file = new File(fname);
		Scanner sc;
		String line = null;

		try {
			sc = new Scanner(file);

			if(sc.hasNextLine()) {
				line = sc.nextLine();

				String tokens[] = line.split(" ");

				if(tokens.length == 3){
					this.T = Integer.parseInt(tokens[0]);
					this.n = Integer.parseInt(tokens[1]);
					this.epsilon = Double.parseDouble(tokens[2]);
					this.training_set_x = new double[this.n];
					this.training_set_y = new double[this.n];
					this.training_set_p = new double[this.T+1][this.n];
					this.threshold_array = new double[this.T];
					this.error_array = new double[this.T];
					this.equality_array = new boolean[this.T];
					this.goodness_weight = new double[this.T];
					this.correct_array = new char[this.T][this.n];					
				} else {
					
					System.out.println("Incorrect file input!");
					System.exit(-1);
					
				}				
			} else {
				
				System.out.println("No data in file!");
				System.exit(-1);
				
			}

			if(sc.hasNextLine()) {

				line = sc.nextLine();

				String tokens[] = line.split(" ");

				if(tokens.length == this.n){
					for(int i = 0;i<this.n;i++) {
						this.training_set_x[i] = Double.parseDouble(tokens[i]);
					}	
				} else {
					System.out.println("Incorrect file input!");
					System.exit(-1);
				}		

			} else {
				System.out.println("No data in file!");
				System.exit(-1);
			}


			if(sc.hasNextLine()) {

				line = sc.nextLine();

				String tokens[] = line.split(" ");

				if(tokens.length == this.n){
					for(int i = 0;i<this.n;i++) {
						this.training_set_y[i] = Double.parseDouble(tokens[i]);
					}	
				} else {
					System.out.println("Incorrect file input!");
					System.exit(-1);
				}		

			} else {
				System.out.println("No data in file!");
				System.exit(-1);
			}


			if(sc.hasNextLine()) {

				line = sc.nextLine();

				String tokens[] = line.split(" ");

				if(tokens.length == this.n){
					for(int i = 0;i<this.n;i++) {
						this.training_set_p[0][i] = Double.parseDouble(tokens[i]);		// At index 0 - initial probabilities, from 1 to T+1 - new probabilities
					}	
				} else {
					System.out.println("Incorrect file input!");
					System.exit(-1);
				}		

			} else {
				System.out.println("No data in file!");
				System.exit(-1);
			}

			sc.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	public void findHypothesis(int iteration) {

		double threshold = 0.0;

		double less_than_threshold = 0.0;

		double greater_than_threshold = 0.0;

		double error;

		double less_than_error = Double.MAX_VALUE;

		double greater_than_error = Double.MAX_VALUE;

		boolean useLessThan = true;

		for(int i = 0;i<this.n-1;i++) {

			double local_threshold = (this.training_set_x[i] + this.training_set_x[i+1])/2;

			double local_error = 0.0;

			for(int j = 0; j < this.n;j++) {
				if(this.training_set_x[j] < local_threshold) {
					if(this.training_set_y[j] != 1) {
						local_error += this.training_set_p[iteration][j];			// previous iterations' probabilities
					}
				} else {
					if(this.training_set_y[j] == 1) {
						local_error += this.training_set_p[iteration][j];
					}
				}
			}

			if(local_error<less_than_error) {
				less_than_error = local_error;
				less_than_threshold = local_threshold;
			}
		}

		for(int i = 0;i<this.n-1;i++) {

			double local_threshold = (this.training_set_x[i] + this.training_set_x[i+1])/2;

			double local_error = 0.0;

			for(int j = 0; j < this.n;j++) {
				if(this.training_set_x[j] > local_threshold) {
					if(this.training_set_y[j] != 1) {
						local_error += this.training_set_p[iteration][j];
					}
				} else {
					if(this.training_set_y[j] == 1) {
						local_error += this.training_set_p[iteration][j];
					}
				}
			}

			if(local_error<greater_than_error) {
				greater_than_error = local_error;
				greater_than_threshold = local_threshold;
			}
		}

		if(greater_than_error<less_than_error) {
			error  = greater_than_error;
			threshold = greater_than_threshold;
			useLessThan = false;
		} else {
			error = less_than_error;
			threshold = less_than_threshold;
		}

		this.threshold_array[iteration] = threshold;
		this.error_array[iteration] = error;
		this.equality_array[iteration] = useLessThan;
	}
	
	
	public double compute_normalization_factor(double error) {

		double normalization_factor = 2 * (Math.sqrt(error*(1-error)));

		return normalization_factor;
	}

	
	public double compute_goodness(double error) {

		double goodness_per_iteration = 0.5 * (Math.log((1-error)/error));

		return goodness_per_iteration;
	}
	
	
	public void compute_new_probabilities(int iteration, double threshold_per_iteration, boolean equality_per_iteration, double q_right, double q_wrong, double normalization_factor) {

		for(int i = 0;i<this.n;i++) {
			//  check classifier, whether < or >
			if(equality_per_iteration) {
				// if classifier is <, then x<v should have y=1, rest y=-1 
				if(this.training_set_x[i]<threshold_per_iteration) {

					if(this.training_set_y[i] == 1) {
						this.training_set_p[iteration+1][i] = (this.training_set_p[iteration][i] * q_right)/normalization_factor;
						this.correct_array[iteration][i] = 'y';
					}
					else {
						this.training_set_p[iteration+1][i] = (this.training_set_p[iteration][i] * q_wrong)/normalization_factor;
						this.correct_array[iteration][i] = 'n';
					}

				} else {		

					if(this.training_set_y[i] != 1) {
						this.training_set_p[iteration+1][i] = (this.training_set_p[iteration][i] * q_right)/normalization_factor;
						this.correct_array[iteration][i] = 'y';
					}
					else {
						this.training_set_p[iteration+1][i] = (this.training_set_p[iteration][i] * q_wrong)/normalization_factor;
						this.correct_array[iteration][i] = 'n';
					}

				}		
			} else {	
				// if classifier is >, then x>v should have y=1, rest y=-1
				if(this.training_set_x[i]>threshold_per_iteration) {

					if(this.training_set_y[i] == 1) {
						this.training_set_p[iteration+1][i] = (this.training_set_p[iteration][i] * q_right)/normalization_factor;
						this.correct_array[iteration][i] = 'y';
					}
					else {
						this.training_set_p[iteration+1][i] = (this.training_set_p[iteration][i] * q_wrong)/normalization_factor;
						this.correct_array[iteration][i] = 'n';
					}

				} else {

					if(this.training_set_y[i] != 1) {
						this.training_set_p[iteration+1][i] = (this.training_set_p[iteration][i] * q_right)/normalization_factor;
						this.correct_array[iteration][i] = 'y';
					}
					else {
						this.training_set_p[iteration+1][i] = (this.training_set_p[iteration][i] * q_wrong)/normalization_factor;
						this.correct_array[iteration][i] = 'n';
					}

				}	
			}
		}

//		System.out.println();
//		System.out.println();
//		System.out.println("New probabilities after normalization for interation " + (iteration+1) + " : ");
//		System.out.println("-------------------------------------");
//		System.out.println("index || correct || new probabilities");
//		System.out.println("-------------------------------------");
		
		System.out.print("Pi after normalization = ");
		
		for(int k = 0;k<this.n;k++) {
//			System.out.print(k + "     || ");
//			System.out.print(this.correct_array[iteration][k] + "       || ");
//			System.out.print(this.training_set_p[iteration+1][k]);
//			System.out.println();
			
			System.out.print(this.training_set_p[iteration+1][k]);
			if(k!=this.n-1)
				System.out.print(", ");
			else 
				System.out.print(" ");
		}	
//		System.out.println("-------------------------------------");
		System.out.println();
	}

	
	public double classify_using_boosted_classifier(int example_no,int iteration) {

		double f = 0.0;

		for(int i = 0;i<=iteration;i++) {

			boolean equality = this.equality_array[i];

			//			System.out.print(equality + " ");
			//			System.out.print(this.training_set_x[example_no] + " ");
			//			System.out.print(this.threshold_array[i]);
			//			System.out.println();

			if(equality) {

				if(this.training_set_x[example_no] < this.threshold_array[i])
					f = f + (this.goodness_weight[i] * 1.0);
				else 
					f = f + (this.goodness_weight[i] * (-1.0));		
				
			} else {

				if(this.training_set_x[example_no] > this.threshold_array[i])
					f = f + (this.goodness_weight[i] * 1.0);
				else 
					f = f + (this.goodness_weight[i] * (-1.0));
			}
		}
		return f;
	}
	
	
	public void compute_boosted_classifier(int iteration) {

		double count = 0.0;

//		System.out.println();

		for(int i = 0;i<this.n;i++) {

			double f = classify_using_boosted_classifier(i,iteration);

//			System.out.print("Iteration " + (iteration+1) + " - The training example " + i + " : f(x) = " + f);

			double y = 0.0;

			if(f<0) {
				y = -1.0;				
			} else {
				y = 1.0;
			}

			if(y == this.training_set_y[i]) {
//				System.out.print(" And classified correctly!");
			} else {
				count++;
//				System.out.print(" And classified wrongly!");
			}

//			System.out.println();
		}

//		System.out.println();

//		System.out.println("Mistakes in this iteration " + (iteration+1) + " are : " + count);

		System.out.println("Boosted Classifier Error = " + count/this.n);
//		System.out.println("Error of boosted classifier so far (E) = " + count/this.n);
	}
	
	
	public void runSingleIteration(int iteration) {

		findHypothesis(iteration);

		double threshold_per_iteration = this.threshold_array[iteration];
		double error_per_iteration = this.error_array[iteration];
		boolean equality_per_iteration = this.equality_array[iteration];		// True for representing <=, false for >
		String selected_classifier = "";

		if(equality_per_iteration) {
			selected_classifier = "I(x < " + threshold_per_iteration + ")";
		} else {
			selected_classifier = "I(x > " + threshold_per_iteration + ")";
		}


		System.out.println("Classifier h = " + selected_classifier);
//		System.out.println("Selected weak classifier for iteration " + (iteration+1) + " is : " + selected_classifier);



		System.out.println("Error = " + error_per_iteration);
//		System.out.println("Error of selected classifier " + selected_classifier + " : " + error_per_iteration);



		goodness_weight[iteration] = compute_goodness(error_per_iteration); 
		System.out.println("Alpha = " + goodness_weight[iteration]);
//		System.out.println("Goodness weight of selected classifier " + selected_classifier + " : " + goodness_weight[iteration]);



		double normalization_factor = compute_normalization_factor(error_per_iteration);		
//		System.out.println("The probabilities normalization factor (Z) of selected classifier " + selected_classifier + " : " + normalization_factor);
		System.out.println("Normalization factor Z = " + normalization_factor);



		double q_right = Math.exp(-(goodness_weight[iteration]));
		double q_wrong = Math.exp(goodness_weight[iteration]);		
		compute_new_probabilities(iteration,threshold_per_iteration,equality_per_iteration,q_right,q_wrong,normalization_factor);



		this.boosted_classifier.append(goodness_weight[iteration] + " * " + selected_classifier);
		System.out.println("Boosted Classifier f(x) = " + this.boosted_classifier.toString());
//		System.out.println("Boosted classifier after iteration " + (iteration+1) + " f(x) : " + this.boosted_classifier.toString());
		if(iteration != this.T-1) this.boosted_classifier.append(" + ");

		

		compute_boosted_classifier(iteration);



		this.bound = this.bound * normalization_factor;
		System.out.println("Bound on Error = " + this.bound);
//		System.out.println("Bound on error of boosted classifier (E) : " + this.bound);

	}

	
	// Run Binary Adaptive Boosting 
	public void runBinaryAdaBoosting() {

		for(int i=0;i<this.T;i++) {

//			System.out.println();
			System.out.println();
//			System.out.println("--------------------------------Iteration 1 ------------------------------------------");
			System.out.println("Iteration " + (i+1));

			runSingleIteration(i);		// Run a single iteration

//			System.out.println("--------------------------------------------------------------------------------------");


			if(this.error_array[i] == 0.0) {

				System.out.println("The weak classifier is not weak. Consider a re-run of the algorithm with more examples or with a weaker classifier.");
				System.exit(-1);

			} else if(this.error_array[i] >= 0.5) {

				System.out.println("Select a different subset of the training examples.");
				System.exit(-1);

			}
		}
	}


	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//		double d= 0.000001;
		//		System.out.printf("%f",d);

		Scanner sc = new Scanner(System.in);
		
		BinaryAdaBoosting bab = new BinaryAdaBoosting();
		
//		System.out.println("Enter the full-file path for data file : ");
		
//		bab.readFile(sc.nextLine().toString());		
		
//		bab.readFile(filename);
		
//		bab.readFile("C:/Users/Anvesh/Google Drive/workspace/ML2/src/adaboost-1.dat");
//		bab.readFile("C:/Users/Anvesh/Google Drive/workspace/ML2/src/adaboost-2.dat");
//		bab.readFile("C:/Users/Anvesh/Google Drive/workspace/ML2/src/adaboost-3.dat");
//		bab.readFile("C:/Users/Anvesh/Google Drive/workspace/ML2/src/adaboost-4.dat");
		bab.readFile("C:/Users/Anvesh/Google Drive/workspace/ML2/src/adaboost-5.dat");

//		System.out.println("Contents of the file : ");
//		System.out.println(bab.T + " " + bab.n + " " + bab.epsilon);
//
//		for(int i=0;i<bab.n;i++) {
//			System.out.print(bab.training_set_x[i] + " ");
//			System.out.print(bab.training_set_y[i] + " ");
//			System.out.print(bab.training_set_p[0][i] + " ");
//			System.out.println();
//		}		

		bab.runBinaryAdaBoosting();

		sc.close();
	}

}
