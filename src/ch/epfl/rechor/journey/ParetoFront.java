package ch.epfl.rechor.journey;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.LongConsumer;

/**
 * Représente une frontière de Pareto immuable contenant des critères empaquetés sous forme de valeurs long.
 * <p>
 * Chaque tuple représente un ensemble de critères (par exemple, minutes d'arrivée, nombre de changements, charge utile, et éventuellement minutes de départ)
 * et est stocké dans un tableau immuable.
 * </p>
 *
 * @author Ismael Errhaimini (391287)
 * @author Mahdei Mohamed El Arabi (397844)
 */
public class ParetoFront {
    private long[] tuples;

    /**
     * Une frontière de Pareto vide.
     */
    final public static ParetoFront EMPTY = new ParetoFront(new long[0]);

    /**
     * Constructeur privé.
     *
     * @param tab le tableau de tuples qui constitue la frontière de Pareto.
     */
    private ParetoFront(long[] tab){
        this.tuples = Arrays.copyOf(tab,tab.length);
    }

    /**
     * Retourne le nombre de tuples présents dans cette frontière.
     *
     * @return le nombre de tuples.
     */
    public int size(){
        return tuples.length;
    }

    /**
     * Recherche et retourne le tuple dont les minutes d'arrivée et le nombre de changements sont spécifiés.
     *
     * @param arrMins les minutes d'arrivée recherchées.
     * @param changes le nombre de changements recherchés.
     * @return le tuple correspondant aux critères.
     * @throws NoSuchElementException si aucun tuple ne correspond aux critères.
     */
    public long get(int arrMins, int changes) {
        for (long i : tuples) {
            if (PackedCriteria.arrMins(i) == arrMins && PackedCriteria.changes(i) == changes) {
                return i;
            }
        }
        throw new NoSuchElementException();
    }

    /**
     * Applique l'action spécifiée à chaque tuple de cette frontière.
     *
     * @param action l'action à effectuer sur chaque tuple.
     */
    public void forEach(LongConsumer action){
        for(long element : tuples){
            action.accept(element);
        }
    }

    /**
     * Retourne une représentation textuelle de cette frontière de Pareto.
     * <p>
     * Chaque tuple est affiché sous la forme "depMins arrMins changes payload\r\n" et
     * les tuples sont triés dans l'ordre croissant (ordre lexicographique des valeurs empaquetées).
     * </p>
     *
     * @return la chaîne de caractères représentant la ParetoFront.
     */
    @Override
    public String toString() {
        long[] sorted = Arrays.copyOf(tuples, tuples.length);
        Arrays.sort(sorted);
        StringBuilder sb = new StringBuilder();
        for (long tuple : sorted) {
            int dep = PackedCriteria.hasDepMins(tuple) ? PackedCriteria.depMins(tuple) : 0;
            int arr = PackedCriteria.arrMins(tuple);
            int ch = PackedCriteria.changes(tuple);
            int p = PackedCriteria.payload(tuple);
            sb.append(String.format("%d %d %d %d\r\n", dep, arr, ch, p));
        }
        return sb.toString();
    }

    /**
     * Builder pour construire progressivement une instance de ParetoFront.
     */
    public static class Builder {
        private long[] tabBuilder;

        /**
         * Crée un nouveau Builder vide.
         */
        public Builder(){
            tabBuilder = new long[0];
        }

        /**
         * Constructeur de copie.
         *
         * @param that le Builder à copier.
         */
        public Builder(Builder that){
            tabBuilder = Arrays.copyOf(that.tabBuilder, that.tabBuilder.length);
        }

        /**
         * Vérifie si ce Builder est vide.
         *
         * @return true si aucun tuple n'est présent, false sinon.
         */
        public boolean isEmpty(){
            return tabBuilder.length == 0;
        }
        /**
         * Returns a copy of all the tuples currently stored in this builder.
         */
        public long[] getAll() {
            return Arrays.copyOf(tabBuilder, tabBuilder.length);
        }

        /**
         * Vide ce Builder.
         *
         * @return ce Builder (pour chaînage).
         */
        public Builder clear(){
            tabBuilder = new long[0];
            return this;
        }

        /**
         * Ajoute un tuple empaqueté à ce Builder.
         * <p>
         * Si un tuple déjà présent domine ou est égal au tuple à ajouter, l'ajout est ignoré.
         * Si le nouveau tuple domine un ou plusieurs tuples existants, ceux-ci sont supprimés.
         * </p>
         *
         * @param packedTuple le tuple empaqueté à ajouter.
         * @return ce Builder (pour chaînage).
         */
        public Builder add(long packedTuple){
            for(long element : tabBuilder){
                if (PackedCriteria.dominatesOrIsEqual(element, packedTuple)){
                    return this;
                }
            }
            for (int i = tabBuilder.length - 1; i >= 0; i--) {
                if (PackedCriteria.dominatesOrIsEqual(packedTuple, tabBuilder[i])){
                    long[] newtab = new long[tabBuilder.length - 1];
                    for (int j = 0; j < i; j++) {
                        newtab[j] = tabBuilder[j];
                    }
                    for (int j = i + 1; j < tabBuilder.length; j++) {
                        newtab[j - 1] = tabBuilder[j];
                    }
                    tabBuilder = newtab;
                }
            }
            long[] newtab = new long[tabBuilder.length + 1];
            for (int i = 0; i < tabBuilder.length; i++) {
                newtab[i] = tabBuilder[i];
            }
            newtab[newtab.length - 1] = packedTuple;
            tabBuilder = newtab;
            return this;
        }

        /**
         * Empaquète les critères donnés et les ajoute à ce Builder.
         *
         * @param arrMins les minutes d'arrivée.
         * @param changes le nombre de changements.
         * @param payload la charge utile.
         * @return ce Builder (pour chaînage).
         */
        public Builder add(int arrMins, int changes, int payload){
            long packedtuple = PackedCriteria.pack(arrMins, changes, payload);
            add(packedtuple);
            return this;
        }

        /**
         * Ajoute tous les tuples d'un autre Builder à ce Builder.
         *
         * @param that le Builder dont les tuples doivent être ajoutés.
         * @return ce Builder (pour chaînage).
         */
        public Builder addAll(Builder that){
            for(long element : that.tabBuilder){
                add(element);
            }
            return this;
        }

        /**
         * Vérifie si tous les tuples du Builder spécifié sont dominés par au moins un tuple de ce Builder,
         * une fois que les minutes de départ ont été fixées à la valeur spécifiée.
         *
         * @param that le Builder dont les tuples doivent être dominés.
         * @param depMins la valeur des minutes de départ à appliquer pour la comparaison.
         * @return true si chaque tuple de "that" est dominé par un tuple de ce Builder, false sinon.
         */
       /* public boolean fullyDominates(Builder that, int depMins) {
            if(that.isEmpty()){
                return true;
            }
            for(long element : tabBuilder){
                int i = 0;
                for (i = 0; i < that.tabBuilder.length; i++) {
                    long entity = PackedCriteria.withDepMins(that.tabBuilder[i], depMins);
                    if(PackedCriteria.dominatesOrIsEqual(entity, element)) break;
                }
                if(i == that.tabBuilder.length ){
                    return true;
                }
            }
            return false;
        }
        */
        public boolean fullyDominates(Builder that, int depMins) {
            if(that.isEmpty()){
                return true;
            }
            for(long element : tabBuilder){
                int i = 0;
                for (i = 0; i < that.tabBuilder.length; i++) {
                    long entity = PackedCriteria.withDepMins(that.tabBuilder[i], depMins);
                    if(PackedCriteria.dominatesOrIsEqual(entity, element)){
                        if(!PackedCriteria.dominatesOrIsEqual(element, entity))break;
                    }
                }
                if(i == that.tabBuilder.length ){
                    return true;
                }
            }
            return false;
        }


        /**
         * Applique l'action spécifiée à chaque tuple contenu dans ce Builder.
         *
         * @param action l'action à appliquer sur chaque tuple.
         */
        public void forEach(LongConsumer action){
            for(long element : tabBuilder){
                action.accept(element);
            }
        }

        /**
         * Construit une ParetoFront à partir des tuples accumulés dans ce Builder.
         *
         * @return une nouvelle instance de ParetoFront contenant les tuples du Builder.
         */
        public ParetoFront build(){
            return new ParetoFront(tabBuilder);
        }

        /**
         * Retourne une représentation textuelle de ce Builder.
         * <p>
         * Le format est une concaténation de lignes, chacune au format "depMins arrMins changes payload\r\n",
         * les tuples étant triés dans l'ordre naturel (croissant).
         * </p>
         *
         * @return la chaîne de caractères représentant ce Builder.
         */
        @Override
        public String toString() {
            long[] sorted = Arrays.copyOf(tabBuilder, tabBuilder.length);
            Arrays.sort(sorted);
            StringBuilder sb = new StringBuilder();
            for (long tuple : sorted) {
                int dep = PackedCriteria.hasDepMins(tuple) ? PackedCriteria.depMins(tuple) : 0;
                int arr = PackedCriteria.arrMins(tuple);
                int ch = PackedCriteria.changes(tuple);
                int p = PackedCriteria.payload(tuple);
                sb.append(String.format("%d %d %d %d\r\n", dep, arr, ch, p));
            }
            return sb.toString();
        }
    }
}
