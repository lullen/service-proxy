package engine.interfaces;

public class EngineHelloResponse {

    public String text;

    public EngineHelloResponse(String text) {
        this.text = text;
    }

    public EngineHelloResponse() {}

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
