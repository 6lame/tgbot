package funbot.obj;

import java.util.function.Consumer;

public class State {
    private final String name;
    private final AccessLvl accessLvl;
    private final String nextState;
    private final ContentType[] contentType;
    private final Consumer<Context> action;

    public State(String name, AccessLvl accessLvl, String nextState, ContentType[] contentType, Consumer<Context> action) {
        this.name = name;
        this.accessLvl = accessLvl;
        this.nextState = nextState;
        this.contentType = contentType;
        this.action = action;
    }

    public ContentType[] getContentType() {
        return contentType;
    }

    public String getName() {
        return name;
    }

    public AccessLvl getAccessLvl() {
        return accessLvl;
    }

    public String getNextState() {
        return nextState;
    }

    public Consumer<Context> getAction() {
        return action;
    }

    public static class StateBuilder{
        private String name;
        private AccessLvl accessLvl;
        private String nextState;
        private ContentType[] contentType;
        private Consumer<Context> action;

        public StateBuilder(){

        }
        public StateBuilder name(String name){
            this.name = name;
            return this;
        }
        public StateBuilder accessLvl(AccessLvl accessLvl){
            this.accessLvl = accessLvl;
            return this;
        }
        public StateBuilder nextState(String nextState){
            this.nextState = nextState;
            return this;
        }
        public StateBuilder contentType(ContentType... contentType){
            this.contentType = contentType;
            return this;
        }
        public StateBuilder action(Consumer<Context> action){
            this.action = action;
            return this;
        }
        public State build(){
            return new State(name, accessLvl, nextState, contentType, action);
        }
    }
}

