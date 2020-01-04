package E05;

public class SendMessage {
    public static void main(String[] args) {
        Mensagem m = new Mensagem();
        m.storeMsg("userA", "Isto vai ser fácil");
        m.storeMsg("userC", "Não, não vai");
    }
}
