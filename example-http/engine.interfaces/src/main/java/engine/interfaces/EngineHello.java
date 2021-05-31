package engine.interfaces;

public class EngineHello {
    public String text;

    public EngineHello(String text) {
        this.text = text;
    }
    public EngineHello() {
    }


    public void setText(String text) {
        this.text = text;
    }
    
    public String getText() {
        return this.text;
    }
}
