public class Replica{
    private String nombre;
    private InterfazDonacion interfazReplica;

    public Replica(){}

    public Replica(String nombre){
        this.nombre = nombre;
    }

    public Replica(String nombre, InterfazDonacion server){
        this.interfazReplica = server;
        this.nombre = nombre;
    }

    public String getNombre() {
        return this.nombre;
    }

    public InterfazDonacion getInterfaz() {
        return this.interfazReplica;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public void setInterfaz(InterfazDonacion interfazReplica){
        this.interfazReplica = interfazReplica;
    }

}
