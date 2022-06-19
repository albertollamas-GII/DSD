/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// contador.java
package contador;
import icontador.icontador;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.net.MalformedURLException;

public class contador extends UnicastRemoteObject implements icontador {
private int suma;
public contador() throws RemoteException{
}
@Override
public int sumar() throws RemoteException {
return suma;
}
@Override
public void sumar(int valor) throws RemoteException {
suma = valor;
}
@Override
public int incrementar() throws RemoteException {
suma++;
return suma;
}
}
