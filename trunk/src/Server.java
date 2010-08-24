// Aguarda pedido de conexão de algum cliente
// Lê uma linha do cliente
// Transforma as minúsculas em maiúsculas
// Envia ao cliente
// Volta para o início
import java.io.*;
import java.net.*;

class Server {
	public static void main(String argv[]) throws Exception {
		String clientSentence;
		String capitalizedSentence;
		// cria socket de comunicação com os clientes na porta 6789
		ServerSocket welcomeSocket = new ServerSocket(6789);
		// espera msg de algum cliente e trata

		// espera conexão de algum cliente
		Socket connectionSocket = welcomeSocket.accept();
		// cria streams de entrada e saida com o cliente que chegou
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(
				connectionSocket.getInputStream()));
		DataOutputStream outToClient = new DataOutputStream(
				connectionSocket.getOutputStream());
		// lê uma linha do cliente
		clientSentence = inFromClient.readLine();
		// transforma a linha em maiúsculas
		capitalizedSentence = clientSentence.toUpperCase() + '\n';
//		System.out.println(clientSentence);
		// envia a linha maiúscula para o cliente
		outToClient.writeBytes(capitalizedSentence);

		while ((clientSentence = inFromClient.readLine()) != null) {
			capitalizedSentence = clientSentence.toUpperCase() + '\n';
//			System.out.println(clientSentence);
			// envia a linha maiúscula para o cliente
			outToClient.writeBytes(capitalizedSentence);
		}
	}
}
