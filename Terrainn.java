//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

public class Terrainn extends PhysicsCoefficients {
    cell[][] field;
    int x_width;
    //int y_height;
    int z_width;

    Terrainn(int x_width, int y_height, int z_width) {
        this.x_width = x_width;
        //this.y_height = y_height;
        this.z_width = z_width;
        this.field = new cell[x_width][z_width];
    }

    public  void terrain_generator() {
        System.out.println("works");
        for(int i=0;i<x_width;i++){
            field[i][0]=new cell("wall",0,0);
            System.out.println("works1");
        }
        for(int i=0;i<x_width;i++){
            field[i][z_width-1]=new cell("wall",0,0);
            System.out.println("works2");
        }
        for(int i=1;i<z_width-1;i++){
            field[0][i]=new cell("wall",0,0);
            System.out.println("works3");
        }
        for(int i=1;i<z_width-1;i++){
            field[x_width-1][i]=new cell("wall",0,0);
            System.out.println("works4");
        }
    }

    public static void water() {
    }

    public static void grass() {
    }

    public static void sand() {
    }
    public static void wall(){}

    public static void hole(double radius) {
    }

    public static void spawn() {
    }


    public class cell{
        private String material;
        private  int x_tilt;
        private int z_tilt;
        cell(String material,int x_tilt,int z_tilt){
            this.material=material;
            this.x_tilt=x_tilt;
            this.z_tilt=z_tilt;
        }
        public String get_mat(){
            return material;
        }
    }
    public static void main(String[] args) {
        System.out.println("it works");
        PhysicsCoefficients pcof = new PhysicsCoefficients();
        System.out.println(pcof.vmax);
        Terrainn test=new Terrainn(10,10,10);
        test.terrain_generator();
        System.out.println( test.field[1][0].get_mat());
    }
    public boolean isWater(Vector position) {
        // Define logic to determine if the position is water
        return false; // Placeholder
    }
    public boolean isObstacle(Vector position) {
        // Define logic to determine if the position hits an obstacle
        return false; // Placeholder
    }
}
