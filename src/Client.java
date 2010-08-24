// Abre conexão com o servidor
// Lê uma linha do teclado
// Envia a linha ao servidor
// Lê uma linha do servidor e mostra no vídeo
// Fecha conexão com o servidor
import java.io.*; // classes para input e output streams
import java.net.*; // classes para socket, serversocket e clientsocket

class Client {
	public static void main(String argv[]) throws Exception {
		
		String sentence;
		String modifiedSentence;
		Socket clientSocket = new Socket("localhost", 6789);
		// cria o stream do teclado
		while(true){
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(
				System.in));
		// cria o socket de acesso ao server hostname na porta 6789
		// cria os streams (encadeamentos) de entrada e saida com o servidor
		DataOutputStream outToServer = new DataOutputStream(
				clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		// le uma linha do teclado e coloca em sentence
		sentence = inFromUser.readLine();
		// envia a linha para o server
		outToServer.writeBytes(sentence + '\n');
		// lê uma linha do server
		modifiedSentence = inFromServer.readLine();
		// apresenta a linha do server no vídeo
		System.out.println("FROM SERVER " + modifiedSentence);
		// fecha o cliente
//		clientSocket.close();
		}
//		clientSocket.close();
	}
}