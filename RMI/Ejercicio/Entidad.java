import java.io.Serializable;
public class Entidad implements Serializable{
    private String nombre;
    private String passwd;
    private double totalDonado;

    public Entidad(String nombreEntidad, String passwd){
        this.nombre = nombreEntidad;
        this.passwd = passwd;
        this.totalDonado = 0.0;
    }

    public String getNombre(){
        return this.nombre;
    }
    public String getPassword(){
        return this.passwd;
    }
    public double getTotalDonado(){
        return this.totalDonado;
    }

    public void donarDinero(double dinero){
        this.totalDonado += dinero;
    }

    public String toString(){
        return "Nombre : " + nombre + " Total donado : " + getTotalDonado() + "€.";
    }
}
