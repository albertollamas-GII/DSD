import java.io.Serializable;
public class Registro implements Serializable{
    private String entidad;
    private double donacion;

    public Registro(String entidad, double dinero){
        this.entidad = entidad;
        this.donacion = dinero;
    }
    
    public String getEntidad() {
        return entidad;
    }

    public double getDonacion() {
        return donacion;
    }

    public String toString(){
        return "Entidad " + entidad + ", Dinero donado: " + donacion ;
    }


}
