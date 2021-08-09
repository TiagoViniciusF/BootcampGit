package FuncaoAltOrdem;

import java.util.function.Supplier;

public class Suppliers {
    public static void main(String[] args) {
        //Supplier<Pessoa> instanciaPessoa = () -> new Pessoa();
        Supplier<Pessoa> instanciaPessoa = Pessoa::new;
        System.out.println(instanciaPessoa.get());
    }
}




class Pessoa{
    private String nome;
    private Integer idade;
    public Pessoa() {
        nome = "Tiago";
        idade = 31;
    }
    @Override
    public String toString(){
        return String.format("nome : %s, idade: %d", nome, idade);
    }
}