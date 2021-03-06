import java.util.Scanner;

public class Main {
	private static int GRID_SIYE_X = 6000;
	private static int GRID_SIYE_Y = 6000;
	private static int MAX_BOX_SIYE = 30;
	private static int X_OFFSET = 0;
	private static int Y_OFFSET = 0;
	
	private static int[][] gaussCache = null; //0 is not set, 1 is false, 2 is true
	
	public static void main(String[] args){
		int lowestX = 0;
		int lowestY = 0;
		int highestX = 3000;
		int highestY = 3000;
		
		Scanner scanner = new Scanner(System.in);
        String string = scanner.nextLine();
        String[] str = string.split(" ");
		
		if(str.length > 0){
			lowestX = Integer.parseInt(str[0]);
			highestX = Integer.parseInt(str[1]);
			lowestY = Integer.parseInt(str[2]);
			highestY = Integer.parseInt(str[3]);
		}
		scanner.close();
		
		GRID_SIYE_X = highestX - lowestX;
		GRID_SIYE_Y = highestY - lowestY;
		X_OFFSET = lowestX;
		Y_OFFSET = lowestY;
		
		int mx = -1;
		int my = -1;
		double distanceToCenterSq = Integer.MAX_VALUE;
		int dia = 0;
		
		gaussCache = new int[GRID_SIYE_X+1][GRID_SIYE_Y+1];
		
		Thread t = new Thread(){
			@Override
			public void run(){
				for(int minX = GRID_SIYE_X;minX>=0;minX--){
					for(int minY = GRID_SIYE_Y;minY>=0;minY--){
						isGaussPrime(minX+X_OFFSET, minY+Y_OFFSET);
					}
				}
			}
		};
		t.start(); //Figures out if numbers are gauss primes in the background, starting with the larger (slower to compute) numbers
		
		long startTime = System.currentTimeMillis();
		for(int minX = 0;minX<=GRID_SIYE_X;minX++){
			for(int minY = 0;minY<=GRID_SIYE_Y;minY++){
				int maxDiaLeft = Math.min(GRID_SIYE_X-minX, GRID_SIYE_Y-minY);
				if(maxDiaLeft <= dia){
					break;
				}
				if(!isGaussPrime(minX+X_OFFSET, minY+Y_OFFSET)){
					continue;
				}
				int d = getLargestBoxDiameterPossibleGivenGaussPrime(minX, minY);
				if(d >= dia){
					double distanceSq = Math.pow(minX, 2) + Math.pow(minY, 2);
					boolean update = d>dia || distanceSq < distanceToCenterSq;
					if(update){
						distanceToCenterSq = distanceSq;
						mx = minX+X_OFFSET;
						my = minY+Y_OFFSET;
						dia = d;
					}
				}
			}
		}
		
		if(mx == -1){
			System.out.println("None found");
			t.stop();
			return;
		}
		
		long dur = System.currentTimeMillis() - startTime;
		System.out.println((mx+my));
		t.stop();
		System.out.println("Duration: "+dur+"MS Box: MinX: "+mx+" MinY: "+my+" MaxX: "+(mx+dia)+" MaxY: "+(my+dia));
	}
	
	public static int getLargestBoxDiameterPossibleGivenGaussPrime(int minX, int minY){
		int maxD = Math.min(MAX_BOX_SIYE, Math.min(GRID_SIYE_X-minX, GRID_SIYE_Y-minY));
		
		for(int diameter=1;diameter<=maxD;diameter++){
			boolean g1 = isGaussPrime(minX+diameter+X_OFFSET, minY+diameter+Y_OFFSET); //Top right
			boolean g2 = isGaussPrime(minX+diameter+X_OFFSET, minY+Y_OFFSET); //Bottom right
			boolean g3 = isGaussPrime(minX+X_OFFSET, minY+diameter+Y_OFFSET); //Top left
			
			if(g1 && g2 && g3){
				//Found gauss primes in all corners
				boolean topOk = isRangeClearOfGaussPrimes(minY+diameter+Y_OFFSET, minX+1+X_OFFSET, (minX+diameter)-1+X_OFFSET);
				boolean rightOk = isRangeClearOfGaussPrimes(minX+diameter+X_OFFSET, minY+1+Y_OFFSET, (minY+diameter)-1+Y_OFFSET);
				
				if(topOk && rightOk){
					return diameter;
				}
			}
			if(g3 || g2){
				break;
			}
		}
		
		return 0;
	}
	
	public static boolean isRangeClearOfGaussPrimes(int staticVar, int changeVarMin, int changeVarMax){
		for(int i=changeVarMin;i<=changeVarMax;i++){
			if(isGaussPrime(staticVar, i)){
				return false;
			}
		}
		return true;
	}
	
	public static boolean isGaussPrime(int a, int b){
		int x = a;
		int y = b;
		if(a>b){ //Since order doesn't matter, we can use this to halve the number of gauss prime calculations
			x = b;
			y = a;
		}
		
		int cached = gaussCache[x-X_OFFSET][y-Y_OFFSET];
		if(cached != 0){
			return cached == 2;
		}
		
		if(a != 0 && b != 0){
			boolean bool = isPrime(x*x+y*y);
			gaussCache[x-X_OFFSET][y-Y_OFFSET] = bool ? 2:1;
			return bool;
		}
		
		int abs;
		if(x == 0){
			abs = Math.abs(y);
		}
		else {
			abs = Math.abs(x);
		}
		
		boolean bool = isPrime(abs) && abs % 4==3;
		gaussCache[x-X_OFFSET][y-Y_OFFSET] = bool ? 2:1;
		return bool;
	}
	
	public static boolean isPrime(int n){
		if(n == 1 || n == 2 || n ==3){
			return true;
		}
		if(n % 2 == 0){
			return false;
		}
		if(n % 3 == 0){
			return false;
		}
		
		int i = 5;
		int w = 2;
		
		while(i*i <= n){
			if(n % i == 0){
				return false;
			}
			
			i += w;
			w = 6-w;
		}
		
		return true;
	}

}
