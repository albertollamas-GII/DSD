import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServidorDonacion extends UnicastRemoteObject implements InterfazDonacion {
    private ArrayList<Entidad> entidades;
    //Registro de donaciones
    private ArrayList<Registro> registros;
    private ArrayList<Replica> replicas;
    //Nos dice si esta bloqueado, es decir si 
    private boolean bloqueado;
    //Va a ser localhost por defecto
    private String host;
    //Nombre del servidor que crea el registro en su main 
    private String server_principal;
    

    /**
     * @brief Constructor con parametros de la clase
     * @param host
     * @param principal
     * @param servidores
     * @throws RemoteException
     */

    public ServidorDonacion(String host, String principal, ArrayList<String> servidores) throws RemoteException{
        this.host = host;
        this.server_principal = principal;

        bloqueado = false;
        replicas = new ArrayList<>();
        for (int i = 0; i < servidores.size(); i++) {
            replicas.add(new Replica(servidores.get(i)));
        }

        
        entidades = new ArrayList<>();
        registros = new ArrayList<>();
    }

    /**
     * @brief Registra la entidad con el nombre pasado por parametros en la replica correspondiente
     * @param entidad
     * @throws RemoteException
     */
    @Override
    public void registrarEntidad(String entidad, String contrasenia) throws RemoteException{
        boolean registrada = compruebaRegistro(entidad);
        //Desbloqueamos el servidor en caso de estar bloqueado
        desbloquear();

        if(!registrada){

            InterfazDonacion servidorRegistro = this;
            int numRegistrados = entidades.size();
            
            /*
            Comprobamos primero si el numero de entidades registradas en la replica
            y vemos en que replica servidor estamos. Registraremos la entidad 
            en la replica con menos entidades registradas tal y como dice el enunciado
            */
            for (int i = 0; i < replicas.size(); i++){
                this.getReplica(replicas.get(i));
                if (replicas.get(i).getInterfaz().getNumEntidades() < numRegistrados){
                    numRegistrados = replicas.get(i).getInterfaz().getNumEntidades();
                    servidorRegistro = replicas.get(i).getInterfaz();
                }
            }

            //Si estamos en el servidor registramos la nueva entidad en esta replica
            if(servidorRegistro == this){
                Entidad nuevaEntidad = new Entidad(entidad, contrasenia);
                entidades.add(nuevaEntidad);
                System.out.println("Creada la entidad: " + nuevaEntidad.getNombre());
            }
            else{
                //Si no la registramos en la otra replica con menos entidades registradas
                servidorRegistro.registrarEntidad(entidad, contrasenia);
            }
        }
    }


    /**
     * @brief Iniciamos sesion de la entidad correspondiente
     * @param entidad
     * @return Entidad registrada
     * @throws RemoteException
     */
    @Override
    public Entidad iniciarSesionEntidad(String entidad, String contrasenia) throws RemoteException{
        Entidad res = null;
        if(compruebaRegistro(entidad)){
            System.out.println("Iniciando sesión con " + entidad);
            res = buscarEntidadPorNombre(entidad);
            if (res.getPassword().equals(contrasenia))
                return res;
        }
        return res;
    }

    /**
     * @brief funcion que dona a la causa humanitaria
     * @param entidad
     * @param cantidad
     * @throws RemoteException
     */
    @Override
    public void donar(String entidad, double cantidad) throws RemoteException{
        //Comprobamos que la entidad esta registrada y que la cantidad es mayor que 0
        if(compruebaRegistro(entidad) && cantidad > 0.0){
            //Obtenemos la entidad para trabajar sobre ella
            Entidad resultado = buscarEntidadPorNombre(entidad);
            System.out.println(resultado.getNombre() + " ha donado " + cantidad + "€ a la causa ");
            resultado.donarDinero(cantidad);
            //Modificamos la entidad para actualizar el total donado
            this.añadirEntidad(resultado);
            //Desbloqueamos en caso de estar bloqueado
            this.desbloquear();
            //Añadimos la donacion al registro de donaciones del servidor
            registros.add(new Registro(entidad, cantidad));

        }
        else if (cantidad < 0.0)
            System.err.println("No puedes sacar dinero introduce una cantidad positiva");
    
    }

    /**
     * @brief Comprueba que la entidad @param entidad esta registrada
     * @param entidad
     * @return true o false dependiendo de si esta o no registrada
     * @throws RemoteException
     */
    @Override
    public boolean compruebaRegistro(String entidad) throws RemoteException{
        boolean registrada = false;
        if (buscarEntidadPorNombre(entidad) != null)
            registrada = true;
        
        return registrada;
    }

    /**
     * @brief Busca la entidad en el servidor
     * @param entidad
     * @throws RemoteException
     * @return Entidad buscada
     */
    @Override
    public Entidad buscarEntidadPorNombre(String entidad) throws RemoteException{
        Entidad encontrada = null;

        if (!entidades.isEmpty()) {
            for (int i = 0; i < entidades.size(); i++){
                if (entidades.get(i).getNombre().equals(entidad))
                    encontrada = entidades.get(i); 
            }
        }

        if(encontrada == null){
            //Bloqueo el servidor y procedo a buscar en los otros
            setBloqueado(true);
            if(!replicas.isEmpty()){
                for (int i = 0; i < replicas.size(); i++){
                    this.getReplica(replicas.get(i));
                    if (encontrada == null && !replicas.get(i).getInterfaz().getBloqueado())
                        encontrada = replicas.get(i).getInterfaz().buscarEntidadPorNombre(entidad);
                }
            }
        }
        return encontrada;
    }

    /**
     * @brief Añade la entidad al array de entidades 
     * @param entidad
     * @throws RemoteException
     */
    @Override
    //Usamos synchronized para que se ejecute este metodo de forma secuencial y no vaya como quiera
    public synchronized void añadirEntidad(Entidad entidad) throws RemoteException{
        boolean encontrada = false;

        if (!entidades.isEmpty()) {
            for (int i = 0; i < entidades.size(); i++){
                if (entidades.get(i).getNombre().equals(entidad.getNombre())){
                    entidades.remove(i);
                    entidades.add(entidad);
                }
            }
        }

        if(!encontrada){
            setBloqueado(true);
            if(!replicas.isEmpty()){
                for (int i = 0; i < replicas.size(); i++){
                    this.getReplica(replicas.get(i));
                    if (!encontrada && !replicas.get(i).getInterfaz().getBloqueado()) {
                        replicas.get(i).getInterfaz().añadirEntidad(entidad);
                    }
                }
            }
        }
    }

    /**
     * @brief consulta el subtotal de la replica (en ese servidor)
     * @param entidad
     * @throws RemoteException
     * @return double total replica
     */
    @Override
    public double consultarSubtotal(String entidad) throws RemoteException{
        for (int i = 0; i < replicas.size(); i++)
            this.getReplica(replicas.get(i));
        double subtotal = 0.0;
        //Solo se puede consultar si esta registrado y ya ha realizado una donacion
        if(compruebaRegistro(entidad)){
            Entidad resultado = buscarEntidadPorNombre(entidad);
            System.out.println(resultado.toString());
            if (!registros.isEmpty()) {
                for (int i = 0; i < registros.size(); i++) {
                    System.out.println(registros.get(i).toString());
                    //Comprobamos si ha donado la entidad
                    if (registros.get(i).getEntidad().equals(resultado.getNombre()))
                        subtotal += registros.get(i).getDonacion();
                }
            }
        }
        
        return subtotal;
    }

    /**
     * @brief consulta el total donado
     * @param entidad
     * @throws RemoteException
     * @return double total en todos los servidores
    */
    @Override
    public double consultarTotal(String entidad) throws RemoteException{
        double total = 0.0;
        if(compruebaRegistro(entidad))
            total = this.buscarEntidadPorNombre(entidad).getTotalDonado();
            
        return total;
    }

    /**
     * @brief getter registros del servidor
     * @throws RemoteException
     * @return arraylist con el host el servidor donde se dona y la donacion
     */
    @Override
    public ArrayList<String> getRegistros(Entidad entidad) throws RemoteException {
        ArrayList<String> listaRegistros = new ArrayList<String>();
        if (!registros.isEmpty()) {
            for (int i = 0; i < registros.size(); i++) {
                if (registros.get(i).getEntidad().equals(entidad.getNombre())) {
                    listaRegistros.add(host + "\t" + server_principal + "\t" + registros.get(i).getDonacion());
                }
            }
        }
        setBloqueado(true);

        return listaRegistros;
    }

    /**
     * @brief desbloquea todos los servidores
     * @throws RemoteException
     */
    @Override
    public void desbloquear() throws RemoteException{
        this.setBloqueado(false);
        for (int i = 0; i < replicas.size(); i++) {
            replicas.get(i).getInterfaz().setBloqueado(false);
        }
    }

    /**
     * @brief getter size arraylist entidades
     * @throws RemoteException
     * @return entero size arraylist entidades
     */
    @Override
    public int getNumEntidades() throws RemoteException{
        return entidades.size();
    }

    /**
     * @brief Devuelve el nombre del servidor principal (server1,2,..)
     * @return String nombre del servidor
     */
    @Override
    public String getNombre() throws RemoteException {
        return server_principal;
    }

    /**
     * @brief Devuelve la replica actual en la que se esta trabajando
     * @param replica
     * @throws RemoteException
     * @return Replica deseada
     */
    @Override
    public Replica getReplica(Replica replica) throws RemoteException {
        Replica respuesta = replica;
        InterfazDonacion buscada = null;

        try {
            Registry registro = LocateRegistry.getRegistry(host, 1099);
            buscada = (InterfazDonacion) registro.lookup(replica.getNombre());

            if (buscada != null) {
                respuesta.setInterfaz(buscada);
                for (int i = 0; i < replicas.size(); i++) {
                    if (replicas.get(i).getNombre().equals(respuesta.getNombre()))
                        replicas.set(i, respuesta);
                }
            }
        } catch (RemoteException | NotBoundException e) {
            System.out.println(e.toString());
        }

        return respuesta;
    }

    /**
     * @brief Devuelve si el servidor esta bloqueado
     * @return valor del atributo bloqueado
     */
    @Override
    public boolean getBloqueado() {
        return this.bloqueado;
    }

    /**
     * @brief Setter de bloqueado
     * @param bloqueado
     */
    @Override
    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

}
