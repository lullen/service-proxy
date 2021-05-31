package accessone.interfaces;

public class OneHello {

    public String text;

    public OneHello(String text) {
        this.text = text;
    }

    public OneHello() {
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getText() {
        return this.text;
    }
}
