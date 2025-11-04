import java.util.*;

//note: epsilon value is represented by the empty string ""

class FSA {
    private ArrayList<State> states;
    private ArrayList<String> alphabet;
    private int initialstate;
    private ArrayList<Integer> acceptingstates;
    private int nextstateid;

    //class to represent a state 
    private class State{
       private int statenum;
       private ArrayList<Transition> transitions;

        State(int statenum){
            this.statenum = statenum;
            this.transitions = new ArrayList<>();
        }


        // getter and setter methods for statenum
        public int getstatenum(){
            return statenum;
        }

        public void setstatenum(int statenum){
            this.statenum = statenum;         
        }

        public ArrayList<Transition> gettransitions(){
            return transitions;
        }

        public void settransitions(ArrayList<Transition> transitions){
            this.transitions = new ArrayList<>(transitions);
        }

        //naming convention changed to avoid confusion with addTransition function
        public void addtransitionsetter(Transition transition){
            this.transitions.add(transition);
        }
    }

    //class to represenet a transition in the fsa
    private class Transition{
        private int from;
        private String symbol;
        private int destination;

        Transition(int from, String symbol, int destination){
            this.from = from;
            this.symbol = symbol;
            this.destination = destination;
        }

        public int getfrom(){
            return from;
        }

        public String getsymbol(){
            return symbol;
        }

        public int getdestination(){
            return destination;
        }

        public void setfrom(int from){
            this.from = from;
        }

        public void setsymbol(String symbol){
            this.symbol = symbol;
        }

        public void setdestination(int destination){
            this.destination = destination;
        }

        
    }

    public FSA(){
        states = new ArrayList<>();
        alphabet = new ArrayList<>();
        acceptingstates = new ArrayList<>();
        //since no initial sate has been defined yet
        initialstate = -1;
        nextstateid = 0;
    }

    public int addState(boolean startingstate, boolean acceptingstate){
        int id = nextstateid;
        //increments the name of the state when more statess are added
        nextstateid++;

        State newstate = new State(id);
        states.add(newstate);

        if(startingstate){
            initialstate = id;
        }
        if(acceptingstate){
            acceptingstates.add(id);
        }
        return id;
    }


    public void addTransition(int from, String symbol, int destination){
        
        if (symbol == null){
            symbol = "";
        }

        if (!symbol.equals("") && !alphabet.contains(symbol)) {
            alphabet.add(symbol);
        }

        State state = states.get(from);
        state.addtransitionsetter(new Transition(from, symbol, destination));

    }

    public ArrayList<Integer> closure(int statenum) {
        ArrayList<Integer> reachablestates = new ArrayList<>();
        ArrayList<Integer> checklist = new ArrayList<>();

        // FIX 1: The epsilon closure should include the state itself
        reachablestates.add(statenum);
        
        State state = states.get(statenum);

        for (Transition t: state.gettransitions()){
            if (t.getsymbol().equals("")){
                if (!reachablestates.contains(t.getdestination())){
                    reachablestates.add(t.getdestination());
                    checklist.add(t.getdestination());
                }
            }
        }

        while (!checklist.isEmpty()){
            int currentstate = checklist.remove(0);
            State current = states.get(currentstate);

            for (Transition t : current.gettransitions()){
                if(t.getsymbol().equals("") && !reachablestates.contains(t.getdestination())){
                    reachablestates.add(t.getdestination());
                    checklist.add(t.getdestination());
                }
            }
        }
        return reachablestates;
    }

    public ArrayList<Integer> next(int statenum, String symbol){

        ArrayList<Integer> reachablestates = new ArrayList<>();
        
        // First, get the epsilon closure of the starting state
        ArrayList<Integer> startStates = new ArrayList<>();
        startStates.add(statenum);
        startStates.addAll(closure(statenum));

        // From all states in the epsilon closure, find transitions with the given symbol
        for (int s : startStates) {
            State state = states.get(s);
            
            for (Transition t: state.gettransitions()){
                if (t.getsymbol().equals(symbol)){
                    int dest = t.getdestination();
                    if (!reachablestates.contains(dest)){
                        reachablestates.add(dest);
                    }
                    // Add epsilon closure of the destination state
                    ArrayList<Integer> epsilonstates = closure(dest);
                    for (int e : epsilonstates) {
                        if (!reachablestates.contains(e)) {
                            reachablestates.add(e);
                        }
                    }
                }
            }
        }

        return reachablestates;

    }

    public boolean accepts(String input) {
        if (initialstate == -1) return false;

        ArrayList<Integer> currentstates = new ArrayList<>();
        currentstates.add(initialstate);
        
        // FIX 2: Since closure() now includes the state itself, we don't need to add it separately
        ArrayList<Integer> initialClosure = closure(initialstate);
        for (int s : initialClosure) {
            if (!currentstates.contains(s)) {
                currentstates.add(s);
            }
        }


        for(int i = 0; i < input.length(); i++) {
            String symbol = input.substring(i, i + 1);
            ArrayList<Integer> nextstates = new ArrayList<>();

            for (int state: currentstates) {
                ArrayList<Integer> possible = next(state, symbol);
                for (int p : possible) {
                    if (!nextstates.contains(p)){
                        nextstates.add(p);
                    }
                }
            }

            currentstates = nextstates;
            if (currentstates.isEmpty()){
                return false;
            }
        }

        // FIX 3: Simplified - the epsilon closure is already computed in next()
        // so currentstates already contains all reachable states including epsilon closures
        for (int state : currentstates) {
            if (acceptingstates.contains(state)){
                return true;
            }
        }
        return false;
    }

    public boolean deterministic(){
        for (State state: states){

            ArrayList<String> seen = new ArrayList<>();

           for (Transition t: state.gettransitions()){
                if(t.getsymbol().equals("")){
                    return false;
                }
                if(seen.contains(t.getsymbol())){
                    return false;
                }
                seen.add(t.getsymbol());
        }
    }
    return true;
}

    public FSA toDFA(){
        FSA dfa = new FSA();

        HashMap<String, Integer> statedic = new HashMap<>();
        ArrayList<ArrayList<Integer>> statesets = new ArrayList<>();

        ArrayList<Integer> startset = new ArrayList<>();
        startset.add(initialstate);
        
        // FIX 4: Since closure() now includes the state itself, we need to avoid duplication
        ArrayList<Integer> initialClosure = closure(initialstate);
        for (int s : initialClosure) {
            if (!startset.contains(s)) {
                startset.add(s);
            }
        }

        int dfastate = dfa.addState(true, hasAccepting(startset));
        statedic.put(settostring(startset), dfastate);
        statesets.add(startset);

        int current = 0;
        while (current < statesets.size()){
            ArrayList<Integer> currentset = statesets.get(current);

            for (String symbol : alphabet){
                ArrayList<Integer> nextset = new ArrayList<>();

                // For each state in the current set, find direct transitions on symbol
                for (int state : currentset){
                    State s = states.get(state);
                    for (Transition t : s.gettransitions()) {
                        if (t.getsymbol().equals(symbol)) {
                            int dest = t.getdestination();
                            if (!nextset.contains(dest)) {
                                nextset.add(dest);
                            }
                            // Add epsilon closure of destination
                            ArrayList<Integer> epsilonstates = closure(dest);
                            for (int e : epsilonstates) {
                                if (!nextset.contains(e)) {
                                    nextset.add(e);
                                }
                            }
                        }
                    }
                }

                if (!nextset.isEmpty()){
                    String key = settostring(nextset);
                    if (!statedic.containsKey(key)){
                        int newstate = dfa.addState(false, hasAccepting(nextset));
                        statedic.put(key, newstate);
                        statesets.add(nextset);
                    }

                    dfa.addTransition(statedic.get(settostring(currentset)), symbol, statedic.get(key));

                }
            }
            current++;
        }

        return dfa;
    }

    //helper function to convert states into strings for hashmap
    private String settostring(ArrayList<Integer> stateset){
        Collections.sort(stateset);
        return stateset.toString();
   }

    private boolean hasAccepting (ArrayList<Integer> stateset){
        for (int state: stateset){
            if (acceptingstates.contains(state)){
                return true;
            }
        }
        return false;


}
}
