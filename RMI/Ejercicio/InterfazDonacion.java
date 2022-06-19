import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface InterfazDonacion extends Remote {
    //Funciones basicas
    public void registrarEntidad(String entidad, String contrasenia) throws RemoteException;
    public void donar(String entidad, double cantidad) throws RemoteException;
    public Entidad iniciarSesionEntidad(String entidad, String contrasenia) throws RemoteException;    
    public double consultarTotal(String entidad) throws RemoteException;
    public double consultarSubtotal(String entidad) throws RemoteException;
    //Funciones auxiliares
    public Entidad buscarEntidadPorNombre(String entidad) throws RemoteException;
    public boolean compruebaRegistro(String entidad) throws RemoteException;
    public void setBloqueado(boolean bloqueado) throws RemoteException;
    public void desbloquear() throws RemoteException;
    public void añadirEntidad(Entidad entidad) throws RemoteException;
    //Funciones getters
    public int getNumEntidades() throws RemoteException;
    public boolean getBloqueado() throws RemoteException;
    public Replica getReplica(Replica replica) throws RemoteException;
    public String getNombre() throws RemoteException;
    //Funcion extra
    public ArrayList<String> getRegistros(Entidad entidad) throws RemoteException;
}
