public class Terrainn extends PhysicsCoefficients {
    public cell[][] field;
    int x_width;
    int y_height;
    int z_width;
    String[] material_collection;

    Terrainn(int x_width, int y_height, int z_width) {
        this.x_width = x_width;
        this.y_height = y_height;
        this.z_width = z_width;
        this.field = new cell[x_width][z_width];
        material_collection_filler();
        terrain_boundaries();
    }

    public void terrain_boundaries() {

        for (int i = 0; i < x_width; i++) {
            field[i][0] = new cell("wall", 0, 0, 0);

        }
        for (int i = 0; i < x_width; i++) {
            field[i][z_width - 1] = new cell("wall", 0, 0, 0);

        }
        for (int i = 1; i < z_width - 1; i++) {
            field[0][i] = new cell("wall", 0, 0, 0);

        }
        for (int i = 1; i < z_width - 1; i++) {
            field[x_width - 1][i] = new cell("wall", 0, 0, 0);

        }
    }

    private void material_collection_filler() {
        material_collection = new String[]{"wall", "sand", "grass", "water", "hole", "spawn"};

    }

    public void fillTerrainWithMaterial(String material) {
        for (int i = 1; i < x_width - 1; i++) {
            for (int j = 1; j < z_width - 1; j++) {
                field[i][j] = new cell(material,0,0,0);
            }
        }
    }

    public static void water() {
    }

    public static void grass() {
    }

    public static void sand() {
    }

    public static void wall() {
    }

    public static void hole(double radius) {
    }

    public static void spawn() {
    }

    public class cell {
        private int height;
        private int x_tilt;
        private int z_tilt;
        private String material;

        cell(String material, int x_tilt, int z_tilt, int height) {
            this.height = height;
            this.material = material;
            this.x_tilt = x_tilt;
            this.z_tilt = z_tilt;
        }

        public String get_material() {
            return material;
        }

        public int get_height() {
            return height;
        }

        public int getx_tilt() {
            return x_tilt;
        }

        public int getz_tilt() {
            return z_tilt;
        }
    }

    public void grasland() {
        String material = "grass";//chooose sand if you like

        for (int i = 1; i < x_width - 1; i++) {
            for (int j = 1; j < z_width - 1; j++) {
                field[i][j] = new cell(material, 0, 0, 0);
            }
        }
        field[Math.round(x_width / 2)][2] = new cell("spawn", 0, 0, 0);
        field[Math.round(x_width / 2)][z_width - 2] = new cell("hole", 0, 0, 0);

    }

    public void sandland() {
        String material = "sand";//chooose sand if you like

        for (int i = 1; i < x_width - 1; i++) {
            for (int j = 1; j < z_width - 1; j++) {
                field[i][j] = new cell(material, 0, 0, 0);
            }
        }
        field[Math.round(x_width / 2)][2] = new cell("spawn", 0, 0, 0);
        field[Math.round(x_width / 2)][z_width - 2] = new cell("hole", 0, 0, 0);

    }

    public void hillland() {
        int height = 0;
        field[Math.round(x_width / 2)][2] = new cell("spawn", 0, 0, 0);
        field[Math.round(x_width / 2)][z_width - 2] = new cell("hole", 0, 0, 0);
        String material = "grass";//chooose sand if you like

        for (int i = 1; i < x_width - 1; i++) {
            for (int j = 1; j < z_width - 1; j++) {
                if (true) {

                }
                field[i][j] = new cell(material, 0, 0, 0);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("it works");
        PhysicsCoefficients pcof = new PhysicsCoefficients();
        System.out.println(pcof.vmax);
        Terrainn test = new Terrainn(10, 10, 10);
        test.grasland();
        System.out.println(test.field[4][4].get_material());


    }
}