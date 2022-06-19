import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {
        if(args.length < 1){
            System.err.println("Especifica después de Cliente a qué servidor quieres conectarte: server1, server2 o server3");
            System.exit(-1);
        }

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        Scanner scanner = new Scanner(System.in);
        String servidorDestino = args[0];   // En nuestro caso va a ser localhost
        int opcion;
        Entidad iniciada_sesion = null;
        boolean identificado, salir;

        try {
            Registry registro = LocateRegistry.getRegistry("localhost", 1099);
            InterfazDonacion gestor = (InterfazDonacion)registro.lookup(servidorDestino);
            identificado = false;
            salir = false;
            System.out.println("Estas en el servidor: " + gestor.getNombre());

            while (!salir) {
                if (!identificado) {        
                    System.out.println("\nBienvenida/o! Elige una opción");
                    System.out.println("1 -> Registrar una entidad");
                    System.out.println("2 -> Iniciar sesión (ya debes estar registrado)");
                    System.out.println("-1 -> Salir");

                    opcion = scanner.nextInt();
                    scanner.nextLine();
                    String nombre;

                    switch (opcion) {
                        //Registrar una entidad
                        case 1:
                            System.out.println("Introduce el nombre de la entidad");
                            nombre = scanner.nextLine();
                            System.out.println("Introduce una contraseña");
                            String passwd = scanner.nextLine();
                            if (!gestor.compruebaRegistro(nombre)) {
                               gestor.registrarEntidad(nombre, passwd);
                               System.out.println("Se ha registrado la entidad.\n\n");
                            } else {
                                System.out.println("La entidad ya existe.\n\n");
                            }
                        break;

                        // Iniciar sesion
                        case 2:
                            System.out.println("Introduce el nombre de la entidad");
                            nombre = scanner.nextLine();
                            System.out.println("Introduce la contraseña");
                            passwd = scanner.nextLine();
                            if (gestor.compruebaRegistro(nombre)) {
                                iniciada_sesion = gestor.iniciarSesionEntidad(nombre, passwd);
                                while(iniciada_sesion == null){
                                    System.out.println("Contraseña incorrecta.\n");
                                    System.out.println("Introduce la contraseña");
                                    passwd = scanner.nextLine();
                                    iniciada_sesion = gestor.iniciarSesionEntidad(nombre, passwd);
                                }
                                System.out.println("Ha iniciado sesion correctamente\n\n");
                                identificado = true;
                            } else {
                                System.out.println("La entidad no existe.\n\n");
                            }
                        break;

                        // Salir
                        case -1:
                            salir = true;
                            System.out.println("Nos veremos en otra ocasion!");
                        break;

                        default:
                            System.out.println("Esta opcion no es valida");
                        break;
                    }
                }
                else{
                    System.out.println("Iniciada sesion como " + iniciada_sesion.getNombre() +  ". Elige una opción");
                    System.out.println("1 -> Donar");
                    System.out.println("2 -> Ver cuánto se ha donado en ESTE servidor: " + gestor.getNombre());
                    System.out.println("3 -> Ver cúanto se ha donado en total (3 servidores)");
                    System.out.println("4 -> Ver el historial de donaciones en ESTE servidor: " + gestor.getNombre());
                    System.out.println("-1 -> Volver a menu principal\n");

                    opcion = scanner.nextInt();
                    scanner.nextLine();

                    switch (opcion) {
                        case -1:
                            identificado = false;
                            System.out.println("Hasta otra! " + iniciada_sesion.getNombre());
                        break;

                        //Donar
                        case 1:
                            System.out.println("\nIntroduzca la cantidad a donar: ");
                            double donacion = scanner.nextDouble();
                            gestor.donar(iniciada_sesion.getNombre(), donacion);
                        break;

                        //Ver cuanto se ha donado en servidor que estemos
                        case 2:
                            System.out.println("La entidad " + iniciada_sesion.getNombre() + " ha donado " + gestor.consultarSubtotal(iniciada_sesion.getNombre()) + "€ a la causa en este servidor.\n\n");
                        break;

                        //Donado en total
                        case 3:
                            System.out.println("La entidad " + iniciada_sesion.getNombre() + " ha donado " + gestor.consultarTotal(iniciada_sesion.getNombre()) + "€ a la causa.\n\n");
                            gestor.desbloquear();
                        break;

                        // Historial de donaciones
                        case 4:
                            ArrayList<String> lista = gestor.getRegistros(iniciada_sesion);
                            System.out.println("Host\tNombre servidor\tCantidad\n");
                            for (int i = 0; i < lista.size(); i++) {
                                System.out.println(lista.get(i));
                            }
                            gestor.desbloquear();
                        break;
                        default:
                            System.out.println("Opción inválida");
                        break;
                    }
                }


            }

        } catch (NotBoundException | RemoteException e) {
            System.err.println(e.toString());
        }

        scanner.close();
    }
}
