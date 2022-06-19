import glob
import sys
import numpy as np

from calculadora import Calculadora

from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer

import logging

logging.basicConfig(level=logging.DEBUG)


class CalculadoraHandler:
    def __init__(self):
        self.log = {}

    def ping(self):
        print("me han hecho ping()")

    def suma(self, n1, n2):
        print("sumando " + str(n1) + " con " + str(n2))
        return n1 + n2

    def resta(self, n1, n2):
        print("restando " + str(n1) + " con " + str(n2))
        return n1 - n2

    def producto(self, n1, n2):
        print("multiplicando " + str(n1) + " con " + str(n2))
        return n1 * n2

    def division(self, n1, n2):
        print("dividiendo " + str(n1) + " entre " + str(n2))
        return n1 / n2

    def sumaVectores(self, vector1, vector2):
        print(f"sumando {str(vector1)} + {str(vector2)} ")
        return np.array(vector1) + np.array(vector2)

    def restaVectores(self, vector1, vector2):
        print(f"restando {str(vector1)} - {str(vector2)} ")
        return np.array(vector1) - np.array(vector2)

    def productoVectores(self, vector1, vector2):
        print(f"multiplicando {str(vector1)} * {str(vector2)} ")
        return np.array(vector1) * np.array(vector2)

    def divisionVectores(self, vector1, vector2):
        print(f"dividiendo {str(vector1)} / {str(vector2)} ")
        return np.array(vector1) / np.array(vector2)

    def productoEscalar(self, vector1, vector2):
        print(f"haciendo el producto escalar de {str(vector1)} y {str(vector2)} ")
        suma = 0
        for i in range (0,len(vector1)):
            suma = suma + (vector1[i]*vector2[i])
        return suma

    def sumaMatrices(self, matriz1, matriz2):
        print(f"sumando {str(matriz1)} y {str(matriz2)} ")
        return np.array(np.matrix(matriz1) + np.matrix(matriz2))

    def restaMatrices(self, matriz1, matriz2):
        print(f"restando {str(matriz1)} y {str(matriz2)} ")
        return np.array(np.matrix(matriz1) - np.matrix(matriz2))

    def productoMatrices(self, matriz1, matriz2):
        print(f"multiplicando {str(matriz1)} y {str(matriz2)} ")
        return np.array(np.matrix(matriz1) * np.matrix(matriz2))        


if __name__ == "__main__":
    handler = CalculadoraHandler()
    processor = Calculadora.Processor(handler)
    transport = TSocket.TServerSocket(host="127.0.0.1", port=9090)
    tfactory = TTransport.TBufferedTransportFactory()
    pfactory = TBinaryProtocol.TBinaryProtocolFactory()

    server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)

    print("iniciando servidor...")
    server.serve()
    print("fin")
