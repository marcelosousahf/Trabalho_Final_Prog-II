package dao;

import modelo.Usuario;

public class DaoTest {
    
        @org.junit.jupiter.api.Test
        public void testInserir(){
            Usuario u = new Usuario("Marcelo", "Marcelo", "123456");
            Dao<Usuario> dao = new Dao(Usuario.class);
            dao.inserir(u);
       }
    
    
//       @org.junit.jupiter.api.Test
//       public void testListar(){
//           List<Usuario> lista;
//           Dao<Usuario> dao = new Dao(Usuario.class);
//        // dao.excluir("login", "Rafa");
//           lista = dao.listarTodos();
//           for(Usuario u : lista)
//           {
//               System.out.println(u.getLogin());
//           }       
//           
//       }

//         @org.junit.jupiter.api.Test
//        public void testInserir(){
//            Veiculo v = new Veiculo("BDA120", "Ford", "Ford-k");
//            Dao<Veiculo> dao = new Dao(Veiculo.class);
//            dao.inserir(v);
//       }
    
//       @org.junit.jupiter.api.Test
//        public void testListar(){
//           List<Veiculo> lista;
//           Dao<Veiculo> dao = new Dao(Veiculo.class);
//           lista = dao.listarTodos();
//           for(Veiculo v : lista)
//           {
//               System.out.println(v.getPlaca());
//               System.out.println(v.getMarca());
//               System.out.println(v.getModelo());
//           }
//       }
}