package accessone.interfaces;

public class OneHelloResponse {
    
    public String text;

    public OneHelloResponse(String text) {
        this.text = text;
    }
    public OneHelloResponse() {
        
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public String getText() {
        return this.text;
    }
}
