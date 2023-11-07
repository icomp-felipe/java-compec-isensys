package compec.ufam.isensys.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import compec.ufam.isensys.model.retorno.ListaRetornos;
import compec.ufam.isensys.model.retorno.Retorno;
import compec.ufam.isensys.model.retorno.Similaridade;

public class JaroWinkler {

	/** Calcula a lista de distância e similaridades. */
	public static List<Similaridade> compute(final ListaRetornos listaRetornos, final ListaRetornos listaRecursos) {
		
		// Lista de distância e similaridades
		List<Similaridade> listaSimilaridades = null;
		
		// Recuperando apenas os candidatos deferidos no recurso 
		List<Retorno> recursosDeferidos = listaRecursos.getList().stream().filter(Retorno::deferido).toList();
		
		// Se 'recursosDeferidos == null' significa que não houve recurso ou ninguém foi deferido
		if (recursosDeferidos != null) {
		
			// Inicializando os algoritmos
			final JaroWinklerDistance   algoDistancia    = new JaroWinklerDistance  ();
			final JaroWinklerSimilarity algoSimilaridade = new JaroWinklerSimilarity();
			
			// Inicializando a lista de distância e similaridades
			listaSimilaridades = new ArrayList<Similaridade>();
			
			// Recuperando a lista geral de solicitações de isenção
			List<Retorno> listaIsencao = listaRetornos.getList();
			
			// Para cada candidato com deferimento no recurso...
			for (Retorno deferidoRecurso: recursosDeferidos) {

				// ...recupero sua solicitação original...
				Retorno retornoIsencao = listaIsencao.stream().filter(retorno -> retorno.equals(deferidoRecurso)).findFirst().orElse(null);
				
				// ...e realizo o cálculo de distância com os nomes informados
				String nomeAnterior = retornoIsencao.getNomeAnterior();
				String nomeAtual    = deferidoRecurso.getNome();
				double distancia    = algoDistancia.apply(nomeAnterior, nomeAtual);
				double similaridade = algoSimilaridade.apply(nomeAnterior, nomeAtual);
				
				Similaridade sim = new Similaridade(nomeAnterior, nomeAtual, distancia, similaridade);
				listaSimilaridades.add(sim);
				
			}

			// Após o preenchimento da lista, realizo a ordenação de acordo com a similaridade
			Comparator<Similaridade> comparator = (sim1, sim2) -> Double.compare(sim1.getSimilaridade(), sim2.getSimilaridade());
			
			Collections.sort(listaSimilaridades, comparator);
			
		}
			
		return listaSimilaridades;
		
	}
	
}