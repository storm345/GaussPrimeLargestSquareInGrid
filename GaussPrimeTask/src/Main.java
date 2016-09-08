
public class Main {
	private static int GRID_SIZE_X = 6000;
	private static int GRID_SIZE_Y = 6000;
	private static int MAX_BOX_SIZE = 30;
	private static int X_OFFSET = 0;
	private static int Z_OFFSET = 0;
	
	private static int[][] gaussCache = null; //0 is not set, 1 is false, 2 is true
	
	public static void main(String[] args){
		int lowestX = 4000; //TODO Input these via args
		int lowestZ = 4000;
		int highestX = 6000;
		int highestZ = 6000;
		
		GRID_SIZE_X = highestX - lowestX;
		GRID_SIZE_Y = highestZ - lowestZ;
		X_OFFSET = lowestX;
		Z_OFFSET = lowestZ;
		
		int mx = -1;
		int mz = -1;
		double distanceToCenterSq = Integer.MAX_VALUE;
		int dia = 0;
		
		gaussCache = new int[GRID_SIZE_X+1+X_OFFSET][GRID_SIZE_Y+1+Z_OFFSET];
		
		long startTime = System.currentTimeMillis();
		for(int minX = 0;minX<=GRID_SIZE_X;minX++){
			for(int minZ = 0;minZ<=GRID_SIZE_Y;minZ++){
				int maxDiaLeft = Math.min(GRID_SIZE_X-minX, GRID_SIZE_Y-minZ);
				if(maxDiaLeft <= dia){
					break;
				}
				if(!isGaussPrime(minX+X_OFFSET, minZ+Z_OFFSET)){
					continue;
				}
				int d = getLargestBoxDiameterPossibleGivenGaussPrime(minX, minZ);
				if(d >= dia){
					double distanceSq = Math.pow(minX, 2) + Math.pow(minZ, 2);
					boolean update = d>dia || distanceSq < distanceToCenterSq;
					if(update){
						distanceToCenterSq = distanceSq;
						mx = minX+X_OFFSET;
						mz = minZ+Z_OFFSET;
						dia = d;
					}
				}
			}
		}
		
		long dur = System.currentTimeMillis() - startTime;
		System.out.println("Duration: "+dur+"MS Min X: "+mx+" Min Z: "+mz+" Max X: "+(mx+dia)+" Max Z: "+(mz+dia)+" FINAL OUTPUT: "+(mx+mz));
	}
	
	public static int getLargestBoxDiameterPossibleGivenGaussPrime(int minX, int minZ){
		int maxD = Math.min(MAX_BOX_SIZE, Math.min(GRID_SIZE_X-minX, GRID_SIZE_Y-minZ));
		
		for(int diameter=1;diameter<=maxD;diameter++){
			boolean g1 = isGaussPrime(minX+diameter+X_OFFSET, minZ+diameter+Z_OFFSET); //Top right
			boolean g2 = isGaussPrime(minX+diameter+X_OFFSET, minZ+Z_OFFSET); //Bottom right
			boolean g3 = isGaussPrime(minX+X_OFFSET, minZ+diameter+Z_OFFSET); //Top left
			
			if(g1 && g2 && g3){
				//Found gauss primes in all corners
				boolean topOk = isRangeClearOfGaussPrimes(minZ+diameter+Z_OFFSET, minX+1+X_OFFSET, (minX+diameter)-1+X_OFFSET);
				boolean rightOk = isRangeClearOfGaussPrimes(minX+diameter+X_OFFSET, minZ+1+Z_OFFSET, (minZ+diameter)-1+Z_OFFSET);
				
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
		if(a>b){ //Since order doesn't matter, we can use this to halve the number of gauss prime calculations
			int c = a;
			a = b;
			b = c;
		}
		
		int cached = gaussCache[a][b];
		if(cached != 0){
			return cached == 2;
		}
		
		if(a != 0 && b != 0){
			boolean bool = isPrime(a*a+b*b);
			gaussCache[a][b] = bool ? 2:1;
			return bool;
		}
		
		int abs;
		if(a == 0){
			abs = Math.abs(b);
		}
		else {
			abs = Math.abs(a);
		}
		
		boolean bool = isPrime(abs) && abs % 4==3;
		gaussCache[a][b] = bool ? 2:1;
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
