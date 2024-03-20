import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class SIR_Model {

    double susceptible;
    double infected;
    double recovered;

    Random randomNumber = new Random();
    double gamma = randomNumber.nextInt(4) + 1;
    double k = randomNumber.nextInt(4) + 1;
    double mu = 0.001;

    public SIR_Model(double susceptible, double infected, double recovered) {

        susceptible = this.susceptible;
        infected = this.infected;
        recovered = this.recovered;



        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                System.out.println(changeInVariables());
            }
        },0,1000); //Time is in milliseconds


    }

    double s_dot = 0;
    double i_dot = 0;
    double r_dot = 0;

    public String changeInVariables() {



        s_dot = -k * susceptible * infected + mu * (1 - susceptible);
        i_dot = k * susceptible * infected - (gamma + mu) * infected;
        r_dot = gamma * infected - mu * recovered;

        susceptible += s_dot;
        infected += i_dot;
        recovered += r_dot;

        return "There are " + susceptible + " people susceptible, " +
                infected + " infected people and " + recovered + " recovered people.";


    }

    public static void main(String[] args) {

    SIR_Model random = new SIR_Model(10000,400,200);

    }

}
