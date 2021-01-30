import java.util.*;
import java.awt.geom.Point2D;
import java.nio.file.*;


public class Grafo{
    private HashMap<Integer, Set<Arista>> map = new HashMap<>();
    private HashMap<String, String> label_map = new HashMap<>();

    public void addNode(int nodo) {
        if(!map.containsKey(nodo)){
            map.put(nodo, new HashSet<Arista>());
            label_map.put(""+nodo, ""+nodo);
        }
    }

    public void addNodeConnections(int nodo, Set<Arista> nodos_adyacentes) {
        map.put(nodo,nodos_adyacentes);
        for(Arista arista: nodos_adyacentes){
            addEdge(arista);
            label_map.put(""+nodo, ""+nodo);
        }
	}

    public void delNode(int nodo){
        map.remove(nodo);
    }

    public Set<Integer> getNodes() {
        return map.keySet();
    }

    public boolean isLinked(int nodo_origen, int nodo_destino){
        for (Arista aristas: map.get(nodo_origen)){
            if(aristas.nodo_destino == nodo_destino){
                return true;
            }
        }
        return false;
    }

    public Set<Arista> getLinkedNodes(int nodo) {
		return map.get(nodo);
	} 

    public void getNodesCount(){ 
        System.out.println("El grafo tiene "+ map.keySet().size() + " nodos"); 
    }

    public void addEdge(int nodo_origen, int nodo_destino){
        addEdge(new Arista(nodo_origen, nodo_destino));
    }

    public void addEdge(int nodo_origen, int nodo_destino, int peso){
        addEdge(new Arista(nodo_origen, nodo_destino, peso));
    }

    public int getWeight(){
        int peso = 0;
        for (Arista arista: sortEdges(false)){
            peso += arista.peso;
        }
        return peso/2;
    }

    public void addEdge(Arista arista) {
        if (!map.containsKey(arista.nodo_origen)) {
            addNode(arista.nodo_origen);
        }
        if (!map.containsKey(arista.nodo_destino)) {
            addNode(arista.nodo_destino);
        }
        Arista arista_aux = new Arista(arista.nodo_destino, arista.nodo_origen, arista.peso);
        if (!this.isLinked(arista.nodo_origen, arista.nodo_destino)){
            map.get(arista.nodo_origen).add(arista);
            map.get(arista.nodo_destino).add(arista_aux);
        }
    }

    public void delEdge(Arista arista){
        map.get(arista.nodo_origen).remove(arista);
        map.get(arista.nodo_destino).remove(new Arista(arista.nodo_destino, arista.nodo_origen, arista.peso));
    }

    public void getEdgesCount(){ 
        int count = 0; 
        for (Integer v : map.keySet()) { 
            count += map.get(v).size(); 
        } 
        System.out.println("El grafo tiene " + count + " aristas"); 
    } 

    public ArrayList<Arista> sortEdges(boolean inverso){
        Set<Arista> aristas = new HashSet<Arista>();
        ArrayList<Arista> aristas_ordenadas = new ArrayList<Arista>();
        for (Map.Entry<Integer, Set<Arista>> nodos : map.entrySet()) {
            for (Arista arista: nodos.getValue()){
                aristas.add(arista);
            }
        }
        for (Arista arista: aristas){
            aristas_ordenadas.add(arista);
        }
        if (inverso){
            aristas_ordenadas.sort(Comparator.comparing(Arista::getPeso).reversed());
            return aristas_ordenadas;
        }
        else{
            aristas_ordenadas.sort(Comparator.comparing(Arista::getPeso));
            return aristas_ordenadas;
        }
    }

    public int getWeigthEdge(int nodo_origen, int nodo_destino) {
        for (Arista arista : map.get(nodo_origen)) {
            if (arista.nodo_destino==nodo_destino) {
                return arista.peso;
            }
        }
        return (int)Float.POSITIVE_INFINITY;
	}

    public boolean hasNode(int nodo){ 
        if (map.containsKey(nodo)) { 
            return true;
        } 
        else { 
            return false;
        } 
    } 

    public Grafo connected(Grafo grafo, int n) {
        HashMap<Integer, Boolean> explorados = new HashMap<>();
        ArrayList<Integer> queue;
		Grafo g = new Grafo();
		int nodo;
        if(!grafo.hasNode(n)){
			return null;
		}
		for ( int node : grafo.map.keySet() ) {
			explorados.put(node,false);
		}
		queue = new ArrayList<Integer>();
		queue.add(n);
		explorados.put(n, true);
		g.addNode(n);	
		while(queue.size() != 0) {
			nodo = queue.get(0);
			queue.remove(0);
			Set<Arista>neighbors = grafo.getLinkedNodes(nodo);
			for (Arista arista : neighbors) {
				g.addEdge(nodo,arista.nodo_destino,arista.peso);
				if(!explorados.get(arista.nodo_destino)) {
					explorados.put(arista.nodo_destino, true);
					queue.add(arista.nodo_destino);
				}
			}
		}
		return g;
    }

    public Boolean disconnected(Grafo grafo, int nodo_origen, int nodo_destino){
        HashMap<Integer, Boolean> explorados = new HashMap<>();
        ArrayList<Integer>  queue = new ArrayList<Integer>();
        int nodo;
        if (nodo_origen == nodo_destino){
            return false;
        }        
		for ( int node : grafo.map.keySet() ) {
			explorados.put(node,false);
		}
		
		queue.add(nodo_origen);
		explorados.put(nodo_origen, true);
		
		while(queue.size() != 0) {
			nodo = queue.get(0);
			queue.remove(0);
			Set<Arista>neighbors = grafo.getLinkedNodes(nodo);
			for (Arista arista : neighbors) {
				if(!explorados.get(arista.nodo_destino)) {
                    if(nodo_destino == arista.nodo_destino){
                        return false;
                    }
					explorados.put(arista.nodo_destino, true);
					queue.add(arista.nodo_destino);
				}
			}
		}
        return true;
    }
     
    public String toString(){
        StringBuilder res = new StringBuilder("graph abstract {\n");
        for (int nodo : map.keySet() ) {
			res.append("  "+nodo+" [label=\""+label_map.get(""+nodo)+"\"];\n");
        }
        
        for (Map.Entry<Integer,Set<Arista>> n : map.entrySet()){
            int nodo=n.getKey();
            for (Arista arista : n.getValue()) {
                if (nodo<arista.nodo_destino) {
                    res.append("  "+ nodo +" -- "+ arista.nodo_destino + " [label=\""+arista.peso + "\"];\n");
                }
            }
        }
        res.append("}");
        return (res.toString());
    }

    public void toGVFile(String nombre, String grafo){
        Path ubicacion = Paths.get(nombre + ".gv");
        try {
            Files.writeString(ubicacion, grafo);
        } catch (Exception e) {
        }
    }

    public static Grafo genErdosRenyi(int n, int m) {
        Random r = new Random();
        Grafo g = new Grafo();
        for (int i = 0; i < n; i++) {
            g.addNode(i);
        }
        int i = 0;
        int n1 = 0;
        int n2 = 0;
        while(i<m) {
            n1 = r.nextInt(n);
            n2 = r.nextInt(n);
            while(n1 == n2) {
                n2 = r.nextInt(n);
            }
            g.addEdge(n1,n2);
            i++;
        }
        return g;
    }

    public static Grafo genGeografico(int n, double d) {
        Random r = new Random();
        Grafo g = new Grafo();
        Point2D[] xy = new Point2D[n];
        for (int i = 0; i < n; i++) {
            g.addNode(i);
            xy[i] = new Point2D.Double(r.nextDouble(), r.nextDouble());
        }
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                if (i != j) {
                    if (xy[i].distance(xy[j]) < d) {
                        g.addEdge(i,j);
                    }
                }
            }
        }
        return g;
    }

    public static Grafo genGilbert(int n, double p) {
        if (p < 0.0 || p > 1.0)
            throw new IllegalArgumentException("Probabilidad entre 0 y 1");
        Random r = new Random();
        Grafo g = new Grafo();
        for (int i = 0; i < n; i++) {
            g.addNode(i);
        }
        for (int i = 0; i < n - 1 ; i++) {
            for (int j = i + 1; j < n; j++) {
                if (r.nextDouble() <= p) {
                    g.addEdge(i,j);
                }
            }
        }
        return g;
    }

    public static Grafo genBarabasiAlbert(int n, double d) {
        Grafo g = new Grafo();
        Random r = new Random();
        for(int i = 0; i < n; i++) {
			g.addNode(i);
		}
        for (int i = 0; i < d; i++){
            for (int j = i; j < d; j++){
                if(i!=j){
                    g.addEdge(i,j);
                }
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j){
                    if((g.map.get(i).size()) < d && (g.map.get(j).size()) < d){
                        if(r.nextDouble() <= (1-(g.map.get(i).size()/d))){
                            if (g.map.get(i).contains(j) || g.map.get(j).contains(i)){
                            }
                            else{
                                if (Collections.frequency(g.map.values(),i) < d && Collections.frequency(g.map.values(),j) < d){
                                    g.addEdge(i,j);
                                }
                            }
                        }
                    }
                }
            }
        }
        return g;
    }

    public static Grafo Kruskal(Grafo grafo) {
		Grafo g = new Grafo();
        Grafo componente_conectado = grafo.connected(grafo, 0);
        ArrayList<Arista> aristas = componente_conectado.sortEdges(false);
        HashMap<Integer, Integer> marcados = new HashMap<>();
        for (int node : componente_conectado.map.keySet()) {
            marcados.put(node, node);
        }
        for (Arista arista : aristas) {
            if ((marcados.get(arista.nodo_origen) - marcados.get(arista.nodo_destino)) != 0){
                g.addEdge(arista);
                int origen = marcados.get(arista.nodo_origen);
                int destino = marcados.get(arista.nodo_destino);
                for (int marcado:marcados.keySet()) {
                    if(marcados.get(marcado)== origen) {
                        marcados.put(marcado, destino);
                    }
                }
            }
        }
        return g;
    }

    public static Grafo Kruskal_I(Grafo grafo) {
        Grafo g = grafo.connected(grafo, 0);
        ArrayList<Arista> aristas = g.sortEdges(true);
        for (Arista arista : aristas) {
            g.delEdge(arista);
            if(g.disconnected(g, arista.nodo_origen, arista.nodo_destino)){
                g.addEdge(arista);
            }
        }
        return g;
    }

    public static Grafo Prim(Grafo grafo) {
        Grafo g = new Grafo();
        Set<Arista> vecinos = new HashSet<>();
        ArrayList<Integer> nodos = new ArrayList<Integer>();
        ArrayList<Arista> queue = new ArrayList<Arista>();
        Arista aristaNodo;
        int nodo;
        for (Map.Entry n : grafo.map.entrySet()) {
            nodos.add((Integer) n.getKey());
        }
        nodo = nodos.get(0);
        nodos.remove(0);
        vecinos = grafo.getLinkedNodes(nodo);
        for (Arista vecino: vecinos){
            Arista conexion = new Arista(vecino.nodo_origen, vecino.nodo_destino, vecino.peso);
            queue.add(conexion);
        }
        queue.sort(Comparator.comparing(Arista::getPeso));
        while(queue.size() > 0){
            aristaNodo = queue.get(0);
            queue.remove(0);
            if(nodos.indexOf(aristaNodo.nodo_destino) >= 0){
                g.addEdge(aristaNodo);
                nodos.remove(nodos.indexOf(aristaNodo.nodo_destino));
                vecinos = grafo.getLinkedNodes(aristaNodo.nodo_destino);
                for (Arista vecino: vecinos){
                    Arista conexion =  new Arista(vecino.nodo_origen, vecino.nodo_destino, vecino.peso);
                    queue.add(conexion);
                }
                queue.sort(Comparator.comparing(Arista::getPeso));
            }
        }        
        return g;
    }

    

    

    

    



} 
    

