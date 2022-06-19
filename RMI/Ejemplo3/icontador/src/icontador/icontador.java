/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package icontador;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author albertollamas
 */
public interface icontador extends Remote {
    int sumar() throws RemoteException;
    void sumar(int valor) throws RemoteException;
    public int incrementar() throws RemoteException;
}

