public class Terrainn {
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
        //material_collection_filler();
        terrain_boundaries();
    }

    public void terrain_boundaries() {

        for (int i = 0; i < x_width; i++) {
            field[i][0] = new cell("wall", 0, 0);

        }
        for (int i = 0; i < x_width; i++) {
            field[i][z_width - 1] = new cell("wall", 0, 0);

        }
        for (int i = 1; i < z_width - 1; i++) {
            field[0][i] = new cell("wall", 0, 0);

        }
        for (int i = 1; i < z_width - 1; i++) {
            field[x_width - 1][i] = new cell("wall", 0, 0);

        }
    }


    private void material_collection_filler() {
        material_collection = new String[6];
        material_collection[0] = "wall";
        material_collection[1] = "sand";
        material_collection[2] = "gras";
        material_collection[3] = "water";
        material_collection[4] = "hole";
        material_collection[5] = "spawn";
    }






    public class cell {
        private int height;
        private int x_tilt;
        private int z_tilt;
        private String material;

        cell(String material, int x_tilt, int z_tilt) {
            this.height = height;
            this.material = material;
            this.x_tilt = x_tilt;
            this.z_tilt = z_tilt;
        }

        public String get_mat() {
            return material;
        }

        public int getx_tilt() {
            return x_tilt;
        }

        public int getz_tilt() {
            return z_tilt;
        }
    }

    public void grasland() {
        String material = "gras";//chooose sand if you like

        for (int i = 1; i < z_width - 1; i++) {
            for (int j = 1; j < x_width - 1; j++) {
                field[i][j] = new cell(material, 0, 0);
            }
        }
        field[Math.round(x_width / 2)][2] = new cell("spawn", 0, 0);
        field[Math.round(x_width / 2)][z_width - 2] = new cell("hole", 0, 0);

    }


    public void sandland() {
        String material = "sand";//chooose sand if you like

        for (int i = 1; i < x_width - 1; i++) {
            for (int j = 1; j < z_width - 1; j++) {
                field[i][j] = new cell(material, 0, 0);
            }
        }
        field[Math.round(x_width / 2)][2] = new cell("spawn", 0, 0);
        field[Math.round(x_width / 2)][z_width - 2] = new cell("hole", 0, 0);

    }

    public void hillland() {
        int height = 0;
        field[Math.round(x_width / 2)][2] = new cell("spawn", 0, 0);
        field[Math.round(x_width / 2)][z_width - 2] = new cell("hole", 0, 0);
        String material = "gras";//chooose sand if you like

        for (int i = 1; i < x_width - 1; i++) {
            for (int j = 1; j < z_width - 1; j++) {
                if (true) {

                }
                field[i][j] = new cell(material, 0, 0);
            }
        }
    }

    public void default_map() {
        int height = 0;
        for (int i = 1; i < x_width - 1; i++) {
            for (int j = 1; j < z_width - 1; j++) {
                if (true) {

                }
                field[i][j] = new cell("gras", 0, 0);
            }
        }
        int z_level = 1;
        {
            field[1][z_level] = new cell("gras", 0, 0);
            field[2][z_level] = new cell("gras", 0, 0);
            field[3][z_level] = new cell("gras", 0, 0);
            field[4][z_level] = new cell("gras", 0, 0);
            field[5][z_level] = new cell("gras", 0, 0);
            field[6][z_level] = new cell("gras", 0, 0);
            field[7][z_level] = new cell("gras", 0, 0);
            field[8][z_level] = new cell("gras", 0, 0);
        }//z:1
        z_level = 2;
        {
            field[1][z_level] = new cell("water", 0, 1);
            field[2][z_level] = new cell("water", 0, 1);
            field[3][z_level] = new cell("water", 0, 0);
            field[4][z_level] = new cell("gras", 0, 0);
            field[5][z_level] = new cell("gras", 0, 0);
            field[6][z_level] = new cell("gras", 0, 0);
            field[7][z_level] = new cell("gras", 0, 1);
            field[8][z_level] = new cell("gras", 0, 1);
        }//z:2
        z_level = 3;
        {
            field[1][z_level] = new cell("gras", 0, 1);
            field[2][z_level] = new cell("gras", 0, 1);
            field[3][z_level] = new cell("gras", 0, 1);
            field[4][z_level] = new cell("sand", 0, 1);
            field[5][z_level] = new cell("gras", 0, 1);
            field[6][z_level] = new cell("sand", 0, 1);
            field[7][z_level] = new cell("sand", 0, 1);
            field[8][z_level] = new cell("gras", 0, 1);
        }//z:3
        z_level = 4;
        {
            field[1][z_level] = new cell("gras", 0, 1);
            field[2][z_level] = new cell("gras", 0, 1);
            field[3][z_level] = new cell("gras", 0, 1);
            field[4][z_level] = new cell("gras", 0, 2);
            field[5][z_level] = new cell("sand", 0, 3);
            field[6][z_level] = new cell("gras", 0, 2);
            field[7][z_level] = new cell("gras", 0, 1);
            field[8][z_level] = new cell("sand", 0, 1);
        }//z:4
        z_level = 5;
        {
            field[1][z_level] = new cell("gras", 0, 0);
            field[2][z_level] = new cell("gras", 0, 0);
            field[3][z_level] = new cell("gras", 1, 0);
            field[4][z_level] = new cell("sand", 0, 0);
            field[5][z_level] = new cell("gras", 0, -1);
            field[6][z_level] = new cell("gras", 1, -1);
            field[7][z_level] = new cell("gras", 0, 0);
            field[8][z_level] = new cell("gras", 0, 0);
        }//z:5
        z_level = 6;
        {
            field[1][z_level] = new cell("water", 2, -1);
            field[2][z_level] = new cell("water", 0, -1);
            field[3][z_level] = new cell("water", 0, -1);
            field[4][z_level] = new cell("gras", 0, -1);
            field[5][z_level] = new cell("gras", 0, -1);
            field[6][z_level] = new cell("gras", 1, -1);
            field[7][z_level] = new cell("gras", 0, 3);
            field[8][z_level] = new cell("gras", 0, -1);
        }//z:6
        z_level = 7;
        {
            field[1][z_level] = new cell("water", 1, -1);
            field[2][z_level] = new cell("water", 0, -2);
            field[3][z_level] = new cell("gras", 0, -2);
            field[4][z_level] = new cell("gras", 3, 0);
            field[5][z_level] = new cell("gras", 0, 0);
            field[6][z_level] = new cell("gras", 0, -1);
            field[7][z_level] = new cell("sand", 2, -2);
            field[8][z_level] = new cell("sand", 1, -1);
        }//z:7
        z_level = 8;
        {
            field[1][z_level] = new cell("sand", 1, 1);
            field[2][z_level] = new cell("sand", 0, 2);
            field[3][z_level] = new cell("gras", 0, 2);
            field[4][z_level] = new cell("gras", 0, 0);
            field[5][z_level] = new cell("gras", 0, 0);
            field[6][z_level] = new cell("gras", 0, 1);
            field[7][z_level] = new cell("water", 2, 2);
            field[8][z_level] = new cell("water", 1, 1);
        }//z:8


        field[Math.round(x_width / 2)][2] = new cell("spawn", 0, 0);
        field[Math.round(x_width / 2)][z_width - 2] = new cell("hole", 0, 0);

    }

    public void printmap() {
        System.out.println("Current Terrain Layout:");
        String mat = "";
        for (int i = z_width - 1; i >= 0; i--) {
            for (int j = 0; j < x_width; j++) {
                mat = field[j][i].get_mat();
                switch (mat) {
                    case "wall":
                        mat = "x";
                        break;
                    case "gras":
                        mat = "g";
                        break;
                    case "sand":
                        mat = "s";
                        break;
                    case "water":
                        mat = "w";
                        break;
                    case "spawn":
                        mat = "I";
                        break;
                    case "hole":
                        mat = "O";
                        break;


                }
                System.out.print(" " + mat + " ");
            }
            System.out.println("");

        }
        System.out.println("legend wall:x gras:g sand:s water:w spawn:I hole:O");
    }


    public static void main(String[] args) {
        System.out.println("it works");

        Terrainn test = new Terrainn(10, 10, 10);
        test.default_map();
        System.out.println(test.field[4][4].get_mat());
        test.printmap();


    }
}



//create the terrain with the Terrainn constructor and fill in the paramteters(int x_width, int y_height, int z_width)
//it creates an 2d array that has the inserted attributes x_width and z_width, height is not used
// the array consist of the class cell:

//-cell can be constructed with the cell constructor(String material, int x_tilt, intz_tilt)
//-you can read these attributes later by using the get functions for each attribute


//the cells at the edge are filled with wall cells by the terrain_boundaries(); function


//you can create grasland, a complete flat map out of gras or sandland, a complete flat map out of sand or the default map
//every map has spawn and a hole

//if you want to print out the map layout, use the printmap() function to get an overview

