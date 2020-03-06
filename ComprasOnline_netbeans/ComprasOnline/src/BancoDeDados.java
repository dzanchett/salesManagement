
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author diego
 */
public class BancoDeDados {
    
    //Declaração de váriaveis.
    private static String url = "jdbc:mysql://localhost:3306/CompraAqui";
    private static String username = "java";
    private static String password = "1a2b3c";
    
    public static final int ESTADO_ATIVO = 1;
    public static final int ESTADO_INATIVO = 2;
    public static final int ESTADO_BLOQUEADO = 3;
    public static final int ESTADO_BANIDO = 4;
    
    public static final int TIPO_USUARIO_WEB = 1;
    public static final int TIPO_ATENDENTE = 2;
    public static final int TIPO_ADMINISTRADOR = 3;
    public static final int USUARIO_NAO_ENCONTRADO = 4;
    public static final int USUARIO_BLOQUEADO = 5;
    public static final int USUARIO_BANIDO = 6;
    public static final int ERRO_NO_LOGIN = 7;
    
    //Método responsável por criar as tabelas no banco de dados, caso ele não exista.
    public static void createBd() throws SQLException{
        
        //inicializar a comunicacao com o banco de dados
        System.out.println("Carregando driver...");
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver carregado!");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Não foi possivel econtrar o drive!", e);
        }
        
        System.out.println("Conectando-se ao banco de dados");
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            s.execute("CREATE TABLE IF NOT EXISTS usuarios (id INT NOT NULL UNIQUE AUTO_INCREMENT, email VARCHAR(100) NOT NULL, senha VARCHAR(300) NOT NULL, estado INTEGER(1) NOT NULL, tipo INTEGER(1) NOT NULL, PRIMARY KEY(email));");
            s.execute("CREATE TABLE IF NOT EXISTS clientes (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, idUsuario INT NULL, nome VARCHAR(100) NOT NULL, sobrenome VARCHAR(100) NOT NULL, rua VARCHAR(150) NOT NULL, bairro VARCHAR(150) NOT NULL, cidade VARCHAR(150) NOT NULL, uf VARCHAR(150) NOT NULL, cpf CHAR(15) NOT NULL, sexo CHAR(1) NOT NULL, FOREIGN KEY (idUsuario) REFERENCES usuarios(id));");
            s.execute("CREATE TABLE IF NOT EXISTS funcionarios (idUsuario INT NOT NULL PRIMARY KEY, nome VARCHAR(100) NOT NULL, sobrenome VARCHAR(100) NOT NULL, rua VARCHAR(150) NOT NULL, bairro VARCHAR(150) NOT NULL, cidade VARCHAR(150) NOT NULL, uf VARCHAR(150) NOT NULL, cpf CHAR(15) NOT NULL, sexo CHAR(1) NOT NULL);");
            s.execute("CREATE TABLE IF NOT EXISTS categoria (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, nome VARCHAR(100) NOT NULL UNIQUE);");
            s.execute("CREATE TABLE IF NOT EXISTS produtos (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, nome VARCHAR(100) NOT NULL, idCategoria INT NOT NULL, FOREIGN KEY (idCategoria) REFERENCES categoria(id), UNIQUE (nome, idCategoria));");
            s.execute("CREATE TABLE IF NOT EXISTS item (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, quantidade INT NOT NULL, preco REAL(10,2) NOT NULL, idProduto INT NOT NULL, FOREIGN KEY (idProduto) REFERENCES produtos(id));");
            s.execute("CREATE TABLE IF NOT EXISTS pedido (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, data DATE NOT NULL, status VARCHAR(100) NOT NULL);");
            s.execute("CREATE TABLE IF NOT EXISTS carrinho (idPedido INT NOT NULL, idProduto INT NOT NULL, quantidade INT NOT NULL, preco REAL(20,2) NOT NULL, FOREIGN KEY (idPedido) REFERENCES pedido(id), FOREIGN KEY (idProduto) REFERENCES produtos(id));");
            s.execute("CREATE TABLE IF NOT EXISTS conta (idCliente INT NOT NULL, idPedido INT NOT NULL, PRIMARY KEY(idCliente, idPedido), FOREIGN KEY (idCliente) REFERENCES clientes(id), FOREIGN KEY (idPedido) REFERENCES pedido(id));");
            s.execute("CREATE TABLE IF NOT EXISTS pagamento (id INT NULL UNIQUE AUTO_INCREMENT, idPedido INT NOT NULL, nParcelas INT NOT NULL, valorParcela REAL(20,2) NOT NULL, forma VARCHAR(100) NOT NULL, FOREIGN KEY (idPedido) REFERENCES pedido(id));");
            Boolean existiaTabela = s.execute("SELECT * FROM usuarios");
            if(existiaTabela == false){
                s.execute("INSERT INTO usuarios VALUES (null, \"adm@email.com\", \"b09c60fddc573f117449b3723f23d64\", 2, 3);");
                s.execute("INSERT INTO funcionarios VALUES (last_insert_id(), \"Administrador\", \"adm\", \"adm\", \"adm\", \"adm\", \"adm\", \"000.000.000-00\", \"M\");");
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
    }
    
    //Cadastra cliente web no banco de dados.
    public static int cadastroClienteWeb(String nome, String sobrenome, String rua, String bairro, String cidade, String uf, String cpf, String sexo, String email, String senha) throws SQLException, NoSuchAlgorithmException{
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "SELECT id FROM usuarios WHERE email = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, email);
            ResultSet r = stm.executeQuery();
            
            if(r.first() == true){
                return 1;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "SELECT idUsuario FROM clientes WHERE cpf = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, cpf);
            ResultSet r = stm.executeQuery();
            
            if(r.first() == true){
                return 2;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "INSERT INTO usuarios VALUES( null, ?, ?, ?, ?);";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, email);
            stm.setString(2, Encriptador.encriptaSenha(senha));
            stm.setInt(3, ESTADO_ATIVO);
            stm.setInt(4, TIPO_USUARIO_WEB);
            stm.execute();
            
            sql = "SELECT id FROM usuarios WHERE email = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, email);
            ResultSet r = stm.executeQuery();
            
            r.first();
            int id = r.getInt("id");
            
            sql = "INSERT INTO clientes VALUES ( null, ?, ?, ?, ?, ?, ?, ?, ?, ? );";
            stm = connection.prepareStatement(sql);
            stm.setInt(1, id);
            stm.setString(2, nome);
            stm.setString(3, sobrenome);
            stm.setString(4, rua);
            stm.setString(5, bairro);
            stm.setString(6, cidade);
            stm.setString(7, uf);
            stm.setString(8, cpf);
            stm.setString(9, sexo);
            stm.execute();
            
            r.close();
            stm.close();
            connection.close();
            
            TelaLogin.setIdUsusarioLogado(id);
            return 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }        
    }
    
    //Verifica se o usuario e a senha estão iguais a do banco de dados para validar o login.
    public static int loginUsuario(String email, String senha) throws NoSuchAlgorithmException{
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, email);
            stm.setString(2, Encriptador.encriptaSenha(senha));
            ResultSet r = stm.executeQuery();
            
            if(r.first() == true){
                int id = r.getInt("id");
                int estado = r.getInt("estado");
                int tipo = r.getInt("tipo");
                
                switch(estado){
                    case ESTADO_ATIVO:
                    case ESTADO_INATIVO:
                        atualizaEstadoUsuario(id, ESTADO_ATIVO);
                        TelaLogin.setIdUsusarioLogado(id);
                        return tipo;
                    case ESTADO_BLOQUEADO:
                        return USUARIO_BLOQUEADO;
                    case ESTADO_BANIDO:
                        return USUARIO_BANIDO;
                    default:
                        return ERRO_NO_LOGIN;
                }
            }else{
                return USUARIO_NAO_ENCONTRADO;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
    }
    
    //Atualiza o estado do usuário no banco de dados.
    public static void atualizaEstadoUsuario(int id, int estado){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "UPDATE usuarios SET estado = ? WHERE id = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, estado);
            stm.setInt(2, id);
            stm.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
    }
    
    //Obtem todas as categorias cadastradas no banco de dados.
    public static Map<String, Integer> getCategorias(){
        Map<String, Integer> ret = new HashMap<String, Integer>();
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            ResultSet r = s.executeQuery("SELECT * FROM categoria");
            
            while(r.next()){
                int id = r.getInt("id");
                String nome = r.getString("nome");
                
                ret.put(nome, id);
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //adiciona uma categoria ao banco de dados.
    public static void adicionarCategoria(String nome){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "INSERT INTO categoria VALUES ( null, ? );";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, nome);
            stm.execute();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
    }
    
    //cadastra um produto no banco de dados.
    public static Boolean cadastrarProduto(String nome, int idCategoria){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "INSERT INTO produtos VALUES ( null, ?, ? );";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, nome);
            stm.setInt(2, idCategoria);
            stm.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    //Seleciona os produtos de determinada categoria no banco de dados.
    public static Map<String, Integer> getProdutoFromCategoria(int idCategoria){
        Map<String, Integer> ret = new HashMap<String, Integer>();
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "SELECT * FROM produtos WHERE idCategoria = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, idCategoria);
            ResultSet r = stm.executeQuery();
            
            while(r.next()){
                int id = r.getInt("id");
                String nome = r.getString("nome");
                
                ret.put(nome, id);
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //Obtem o preço e quantidade de determinado produto no banco de dados.
    public static List<Object> getItemFromIdProduto(int idItemProduto){
        List<Object> ret = new ArrayList<Object>();
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "SELECT quantidade, preco FROM item WHERE idProduto = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, idItemProduto);
            ResultSet r = stm.executeQuery();
            
            if(r.first()){
                int quantidade = r.getInt("quantidade");
                ret.add(quantidade);
                double preco = r.getDouble("preco");
                ret.add(preco);
            }else{
                ret.add(0);
                ret.add(0.0);
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //Cadastra um item no banco de dados.
    public static Boolean cadastrarItem(int quantidade, double preco, int idProduto){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "SELECT * FROM  item WHERE idProduto = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, idProduto);
            ResultSet r = stm.executeQuery();
            
            if(r.first()){
                sql = "UPDATE item SET quantidade = ?, preco = ? WHERE idProduto = ?;";
                stm = connection.prepareStatement(sql);
                stm.setInt(1, quantidade);
                stm.setDouble(2, preco);
                stm.setInt(3, idProduto);
                stm.execute();
            }else{
                sql = "INSERT INTO item VALUES ( null, ?, ?, ? );";
                stm = connection.prepareStatement(sql);
                stm.setInt(1, quantidade);
                stm.setDouble(2, preco);
                stm.setInt(3, idProduto);
                stm.execute();
            }
            
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    //Cadastra um funcionario no banco de dados.
    public static int cadastroFuncionario(String nome, String sobrenome, String rua, String bairro, String cidade, String uf, String cpf, String sexo, String email, String senha, int tipoFuncionario) throws SQLException, NoSuchAlgorithmException{
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "SELECT id FROM usuarios WHERE email = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, email);
            ResultSet r = stm.executeQuery();
            
            if(r.first() == true){
                return 1;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "SELECT idUsuario FROM funcionarios WHERE cpf = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, cpf);
            ResultSet r = stm.executeQuery();
            
            if(r.first() == true){
                return 2;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "INSERT INTO usuarios VALUES( null, ?, ?, ?, ?);";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, email);
            stm.setString(2, Encriptador.encriptaSenha(senha));
            stm.setInt(3, ESTADO_INATIVO);
            stm.setInt(4, tipoFuncionario);
            stm.execute();
            
            sql = "SELECT id FROM usuarios WHERE email = ?";
            stm = connection.prepareStatement(sql);
            stm.setString(1, email);
            ResultSet r = stm.executeQuery();
            
            r.first();
            int id = r.getInt("id");
            
            sql = "INSERT INTO funcionarios VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? );";
            stm = connection.prepareStatement(sql);
            stm.setInt(1, id);
            stm.setString(2, nome);
            stm.setString(3, sobrenome);
            stm.setString(4, rua);
            stm.setString(5, bairro);
            stm.setString(6, cidade);
            stm.setString(7, uf);
            stm.setString(8, cpf);
            stm.setString(9, sexo);
            stm.execute();
            
            r.close();
            stm.close();
            connection.close();
            
            return 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }        
    }
    
    //cadastra um cliente fisico no banco de dados.
    public static int cadastroClienteFisico(String nome, String sobrenome, String rua, String bairro, String cidade, String uf, String cpf, String sexo) throws SQLException, NoSuchAlgorithmException{
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "SELECT idUsuario FROM clientes WHERE cpf = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, cpf);
            ResultSet r = stm.executeQuery();
            
            if(r.first() == true){
                return 2;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "INSERT INTO clientes VALUES ( null, null, ?, ?, ?, ?, ?, ?, ?, ? );";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, nome);
            stm.setString(2, sobrenome);
            stm.setString(3, rua);
            stm.setString(4, bairro);
            stm.setString(5, cidade);
            stm.setString(6, uf);
            stm.setString(7, cpf);
            stm.setString(8, sexo);
            stm.execute();
            
            stm.close();
            connection.close();
            
            return 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }        
    }
    
    //Seleciona os produtos do banco de dados.
    public static List<String[]> listarProdutos(){
        List<String[]> ret = new ArrayList<String[]>();
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            ResultSet r = s.executeQuery("SELECT A.id, A.nome, B.nome FROM produtos A, categoria B WHERE A.idCategoria = B.id;");
            
            while(r.next()){
                String[] str = new String[3];
                str[0] = r.getString("A.nome");
                str[1] = r.getString("B.nome");
                str[2] = r.getString("A.id");
                ret.add(str);
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //Atualiza algum produto no banco de dados.
    public static void atualizarProdutos(int id, String nome, int idCategoria) throws SQLException, NoSuchAlgorithmException{
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "UPDATE produtos SET nome = ?, idCategoria = ? WHERE id = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, nome);
            stm.setInt(2, idCategoria);
            stm.setInt(3, id);
            stm.execute();
            
            stm.close();
            connection.close();
            
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
    }
    
    //Apaga determinado produto do banco de dados.
    public static int apagarProdutos(int id) throws SQLException, NoSuchAlgorithmException{
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "DELETE FROM produtos WHERE id = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, id);
            stm.execute();
            
            stm.close();
            connection.close();
            
            return 1;
        } catch (SQLException e) {
            return 0;
        }
    }
    
    //Seleciona as categorias no banco de dados.
    public static List<String[]> listarCategorias(){
        List<String[]> ret = new ArrayList<String[]>();
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            ResultSet r = s.executeQuery("SELECT * FROM categoria;");
            
            while(r.next()){
                String[] str = new String[2];
                str[0] = r.getString("nome");
                str[1] = r.getString("id");
                ret.add(str);
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //Atualiza determinada categoria no banco de dados.
    public static void atualizarCategoria(int id, String nome) throws SQLException, NoSuchAlgorithmException{
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "UPDATE categoria SET nome = ? WHERE id = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, nome);
            stm.setInt(2, id);
            stm.execute();
            
            stm.close();
            connection.close();
            
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
    }
    
    //Apaga determinada categoria do banco de dados.
    public static int apagarCategoria(int id) throws SQLException, NoSuchAlgorithmException{
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "DELETE FROM categoria WHERE id = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, id);
            stm.execute();
            
            stm.close();
            connection.close();
            
            return 1;
        } catch (SQLException e) {
            return 0;
        }
    
    }
    
    //Seleciona os itens que estão no estoque, e seus preços e quantidades no banco de dados.
    public static List<String[]> listarEstoque(){
        List<String[]> ret = new ArrayList<String[]>();
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            ResultSet r = s.executeQuery("SELECT A.id, A.quantidade, A.preco, B.nome, C.nome FROM item A, produtos B, categoria C WHERE A.idProduto = B.id AND B.idCategoria = C.id;");
            
            while(r.next()){
                String[] str = new String[5];
                str[0] = r.getString("B.nome");
                str[1] = r.getString("C.nome");
                str[2] = r.getString("A.quantidade");
                str[3] = r.getString("A.preco");
                str[4] = r.getString("A.id");
                ret.add(str);
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //Atualiza determinado item do estoque no banco de dados.
    public static void atualizarEstoque(int id, int quantidade, double preco) throws SQLException, NoSuchAlgorithmException{
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "UPDATE item SET quantidade = ?, preco = ? WHERE id = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, quantidade);
            stm.setDouble(2, preco);
            stm.setInt(3, id);
            stm.execute();
            
            stm.close();
            connection.close();
            
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
    }
    
    //Apaga determinado item do estoque no banco de dados.
    public static int apagarEstoque(int id) throws SQLException, NoSuchAlgorithmException{
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "DELETE FROM item WHERE id = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, id);
            stm.execute();
            
            stm.close();
            connection.close();
            
            return 1;
        } catch (SQLException e) {
            return 0;
        }
    }
    
    //Seleciona todos os usuários do banco de dados.
    public static List<String[]> listarUsuarios(){
        List<String[]> ret = new ArrayList<String[]>();
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            ResultSet r = s.executeQuery("SELECT B.nome, B.sobrenome, B.rua, B.bairro, B.cidade, B.uf, B.cpf, B.sexo, A.email, A.estado, A.tipo, A.id FROM usuarios A, clientes B WHERE B.IdUsuario = A.id;");
            
            while(r.next()){
                String[] str = new String[12];
                str[0] = r.getString("B.nome");
                str[1] = r.getString("B.sobrenome");
                str[2] = r.getString("B.rua");
                str[3] = r.getString("B.bairro");
                str[4] = r.getString("B.cidade");
                str[5] = r.getString("B.uf");
                str[6] = r.getString("B.cpf");
                str[7] = r.getString("B.sexo");
                str[8] = r.getString("A.email");
                
                int estado = r.getInt("A.estado");
                switch(estado){
                    case ESTADO_ATIVO:
                        str[9] = "ativo";
                        break;
                    case ESTADO_INATIVO:
                        str[9] = "inativo";
                        break;
                    case ESTADO_BLOQUEADO:
                        str[9] = "bloqueado";
                        break;
                    case ESTADO_BANIDO:
                        str[9] = "banido";
                        break;
                }
                
                int tipo = r.getInt("A.tipo");
                switch(tipo){
                    case TIPO_USUARIO_WEB:
                        str[10] = "cliente web";
                        break;
                    case TIPO_ATENDENTE:
                        str[10] = "atendente";
                        break;
                    case TIPO_ADMINISTRADOR:
                        str[10] = "administrador";
                        break;
                }
                
                str[11] = r.getString("id");
                
                ret.add(str);
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            ResultSet r = s.executeQuery("SELECT B.nome, B.sobrenome, B.rua, B.bairro, B.cidade, B.uf, B.cpf, B.sexo, A.email, A.estado, A.tipo, A.id FROM usuarios A, funcionarios B WHERE B.IdUsuario = A.id;");
            
            while(r.next()){
                String[] str = new String[12];
                str[0] = r.getString("B.nome");
                str[1] = r.getString("B.sobrenome");
                str[2] = r.getString("B.rua");
                str[3] = r.getString("B.bairro");
                str[4] = r.getString("B.cidade");
                str[5] = r.getString("B.uf");
                str[6] = r.getString("B.cpf");
                str[7] = r.getString("B.sexo");
                str[8] = r.getString("A.email");
                
                int estado = r.getInt("A.estado");
                switch(estado){
                    case ESTADO_ATIVO:
                        str[9] = "ativo";
                        break;
                    case ESTADO_INATIVO:
                        str[9] = "inativo";
                        break;
                    case ESTADO_BLOQUEADO:
                        str[9] = "bloqueado";
                        break;
                    case ESTADO_BANIDO:
                        str[9] = "banido";
                        break;
                }
                
                int tipo = r.getInt("A.tipo");
                switch(tipo){
                    case TIPO_USUARIO_WEB:
                        str[10] = "cliente web";
                        break;
                    case TIPO_ATENDENTE:
                        str[10] = "atendente";
                        break;
                    case TIPO_ADMINISTRADOR:
                        str[10] = "administrador";
                        break;
                }
                
                str[11] = r.getString("id");
                
                ret.add(str);
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //Filtra os usuários, da janela de consulta usuários.
    public static List<String[]> listarUsuariosComFiltro(String type, String valor){
        List<String[]> ret = new ArrayList<String[]>();
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            
            PreparedStatement stm = connection.prepareStatement("SELECT B.nome, B.sobrenome, B.rua, B.bairro, B.cidade, B.uf, B.cpf, B.sexo, A.email, A.estado, A.tipo, A.id FROM usuarios A, clientes B WHERE B.IdUsuario = A.id AND " + type +" = ? ;");
            stm.setString(1, valor);
            ResultSet r = stm.executeQuery();
            
            while(r.next()){
                String[] str = new String[12];
                str[0] = r.getString("B.nome");
                str[1] = r.getString("B.sobrenome");
                str[2] = r.getString("B.rua");
                str[3] = r.getString("B.bairro");
                str[4] = r.getString("B.cidade");
                str[5] = r.getString("B.uf");
                str[6] = r.getString("B.cpf");
                str[7] = r.getString("B.sexo");
                str[8] = r.getString("A.email");
                
                int estado = r.getInt("A.estado");
                switch(estado){
                    case ESTADO_ATIVO:
                        str[9] = "ativo";
                        break;
                    case ESTADO_INATIVO:
                        str[9] = "inativo";
                        break;
                    case ESTADO_BLOQUEADO:
                        str[9] = "bloqueado";
                        break;
                    case ESTADO_BANIDO:
                        str[9] = "banido";
                        break;
                }
                
                int tipo = r.getInt("A.tipo");
                switch(tipo){
                    case TIPO_USUARIO_WEB:
                        str[10] = "cliente web";
                        break;
                    case TIPO_ATENDENTE:
                        str[10] = "atendente";
                        break;
                    case TIPO_ADMINISTRADOR:
                        str[10] = "administrador";
                        break;
                }
                
                str[11] = r.getString("id");
                
                ret.add(str);
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            PreparedStatement stm = connection.prepareStatement("SELECT B.nome, B.sobrenome, B.rua, B.bairro, B.cidade, B.uf, B.cpf, B.sexo, A.email, A.estado, A.tipo, A.id FROM usuarios A, funcionarios B WHERE B.IdUsuario = A.id AND " + type + " = ? ;");
            stm.setString(1, valor);
            ResultSet r = stm.executeQuery();
            
            while(r.next()){
                String[] str = new String[12];
                str[0] = r.getString("B.nome");
                str[1] = r.getString("B.sobrenome");
                str[2] = r.getString("B.rua");
                str[3] = r.getString("B.bairro");
                str[4] = r.getString("B.cidade");
                str[5] = r.getString("B.uf");
                str[6] = r.getString("B.cpf");
                str[7] = r.getString("B.sexo");
                str[8] = r.getString("A.email");
                
                int estado = r.getInt("A.estado");
                switch(estado){
                    case ESTADO_ATIVO:
                        str[9] = "ativo";
                        break;
                    case ESTADO_INATIVO:
                        str[9] = "inativo";
                        break;
                    case ESTADO_BLOQUEADO:
                        str[9] = "bloqueado";
                        break;
                    case ESTADO_BANIDO:
                        str[9] = "banido";
                        break;
                }
                
                int tipo = r.getInt("A.tipo");
                switch(tipo){
                    case TIPO_USUARIO_WEB:
                        str[10] = "cliente web";
                        break;
                    case TIPO_ATENDENTE:
                        str[10] = "atendente";
                        break;
                    case TIPO_ADMINISTRADOR:
                        str[10] = "administrador";
                        break;
                }
                
                str[11] = r.getString("id");
                
                ret.add(str);
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //Atualiza determinado usuario no banco de dados.
    public static void atualizarUsuarios(int idUsuario, int tipo, int estado, String nome, String sobrenome, String rua, String bairro, String cidade, String uf, String cpf, String sexo) throws SQLException, NoSuchAlgorithmException{
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "UPDATE usuarios SET estado = ? WHERE id = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, estado);
            stm.setInt(2, idUsuario);
            stm.execute();
            
            stm.close();
            connection.close();
            
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        if(tipo==TIPO_USUARIO_WEB){
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                System.out.println("Banco de dados conectado!");

                String sql = "UPDATE clientes SET nome = ?, sobrenome = ?, rua = ?, bairro = ?, cidade = ?, uf = ?, cpf = ?, sexo = ? WHERE idUsuario = ?;";
                PreparedStatement stm = connection.prepareStatement(sql);
                stm.setString(1, nome);
                stm.setString(2, sobrenome);
                stm.setString(3, rua);
                stm.setString(4, bairro);
                stm.setString(5, cidade);
                stm.setString(6, uf);
                stm.setString(7, cpf);
                stm.setString(8, sexo);
                stm.setInt(9, idUsuario);
                stm.execute();

                stm.close();
                connection.close();
            } catch (SQLException e) {
                throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
            }
        }else{
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                System.out.println("Banco de dados conectado!");

                String sql = "UPDATE funcionarios SET nome = ?, sobrenome = ?, rua = ?, bairro = ?, cidade = ?, uf = ?, cpf = ?, sexo = ? WHERE idUsuario = ?;";
                PreparedStatement stm = connection.prepareStatement(sql);
                stm.setString(1, nome);
                stm.setString(2, sobrenome);
                stm.setString(3, rua);
                stm.setString(4, bairro);
                stm.setString(5, cidade);
                stm.setString(6, uf);
                stm.setString(7, cpf);
                stm.setString(8, sexo);
                stm.setInt(9, idUsuario);
                stm.execute();

                stm.close();
                connection.close();
            } catch (SQLException e) {
                throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
            }
        }
    }
    
    //Apaga determinado usuario no banco de dados.
    public static void apagarUsuario(int idUsuario, int tipo) throws SQLException, NoSuchAlgorithmException{
        
        if(tipo == TIPO_USUARIO_WEB){
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                System.out.println("Banco de dados conectado!");

                String sql = "DELETE FROM clientes WHERE idUsuario = ?;";
                PreparedStatement stm = connection.prepareStatement(sql);
                stm.setInt(1, idUsuario);
                stm.execute();

                stm.close();
                connection.close();
            } catch (SQLException e) {
                return;
            }
        }else{
            try (Connection connection = DriverManager.getConnection(url, username, password)) {
                System.out.println("Banco de dados conectado!");

                String sql = "DELETE FROM funcionarios WHERE idUsuario = ?;";
                PreparedStatement stm = connection.prepareStatement(sql);
                stm.setInt(1, idUsuario);
                stm.execute();

                stm.close();
                connection.close();
            } catch (SQLException e) {
                return;
            }
        }
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "DELETE FROM usuarios WHERE id = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, idUsuario);
            stm.execute();
            
            stm.close();
            connection.close();
        } catch (SQLException e) {
            return;
        }
    }
    
    //Seleciona os clientes no banco de dados.
    public static List<String[]> listarCliente(){
        List<String[]> ret = new ArrayList<String[]>();
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            ResultSet r = s.executeQuery("SELECT * FROM clientes");
            
            while(r.next()){
                String[] str = new String[10];
                str[0] = r.getString("nome");
                str[1] = r.getString("sobrenome");
                str[2] = r.getString("rua");
                str[3] = r.getString("bairro");
                str[4] = r.getString("cidade");
                str[5] = r.getString("uf");
                str[6] = r.getString("cpf");
                str[7] = r.getString("sexo");
                str[8] = r.getString("idUsuario");
                if(str[8] == null){
                    str[8] = "fisico";
                }else{
                    str[8] = "web";
                }
                str[9] = r.getString("id");
                ret.add(str);
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //Seleciona os dados de determinado cliente no banco de dados.
    public static List<String[]> listarClienteFromId(int idUsuario){
        List<String[]> ret = new ArrayList<String[]>();
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "SELECT * FROM clientes WHERE idUsuario = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, idUsuario);
            ResultSet r = stm.executeQuery();
            
            while(r.next()){
                String[] str = new String[10];
                str[0] = r.getString("nome");
                str[1] = r.getString("sobrenome");
                str[2] = r.getString("rua");
                str[3] = r.getString("bairro");
                str[4] = r.getString("cidade");
                str[5] = r.getString("uf");
                str[6] = r.getString("cpf");
                str[7] = r.getString("sexo");
                str[8] = r.getString("idUsuario");
                if(str[8] == null){
                    str[8] = "fisico";
                }else{
                    str[8] = "web";
                }
                str[9] = r.getString("id");
                ret.add(str);
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //atualiza determinado cliente no banco de dados.
    public static void atualizarCliente(int id, String nome, String sobrenome, String rua, String bairro, String cidade, String uf, String cpf, String sexo) throws SQLException, NoSuchAlgorithmException{
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "UPDATE clientes SET nome = ?, sobrenome = ?, rua = ?, bairro = ?, cidade = ?, uf = ?, cpf = ?, sexo = ? WHERE id = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, nome);
            stm.setString(2, sobrenome);
            stm.setString(3, rua);
            stm.setString(4, bairro);
            stm.setString(5, cidade);
            stm.setString(6, uf);
            stm.setString(7, cpf);
            stm.setString(8, sexo);
            stm.setInt(9, id);
            stm.execute();
            
            stm.close();
            connection.close();
            
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
    }
    
    //apaga determinado cliente do banco de dados.
    public static int apagarCliente(int id) throws SQLException, NoSuchAlgorithmException{
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "DELETE FROM clientes WHERE id = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, id);
            stm.execute();
            
            stm.close();
            connection.close();
            
            return 1;
        } catch (SQLException e) {
            return 0;
        }
    }
    
    //aramazena um pedido no banco de dados.
    public static int gerarPedido(Date data, String status) throws SQLException, NoSuchAlgorithmException{
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "INSERT INTO pedido VALUES ( null, ?, ? );";
            PreparedStatement stm = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stm.setDate(1, new java.sql.Date(data.getTime()));
            stm.setString(2, status);
            stm.execute();
            
            ResultSet r = stm.getGeneratedKeys();
            
            int ret = -1;
            
            if(r.first()){
                ret = r.getInt(1);
            }
            
            stm.close();
            connection.close();
            
            return ret;
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
    }
    
    //aramazena um carrinho de compras no banco de dados.
    public static void gerarCarrinho(int idPedido, int idItem, int quantidade, double preco) throws SQLException, NoSuchAlgorithmException{
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "INSERT INTO carrinho VALUES ( ?, ?, ?, ? );";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, idPedido);
            stm.setInt(2, idItem);
            stm.setInt(3, quantidade);
            stm.setDouble(4, preco);
            stm.execute();
            
            stm.close();
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
    }
    
    //Armazena uma compra, através da tabela conta no banco de dados.
    public static void gerarConta(int idCliente, int idPedido) throws SQLException, NoSuchAlgorithmException{
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "INSERT INTO conta VALUES ( ?, ? );";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, idCliente);
            stm.setInt(2, idPedido);
            stm.execute();
            
            stm.close();
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
    }
    
    //Seleciona os pedidos existentes no banco de dados.
    public static List<String[]> listarPedidos(){
        List<String[]> ret = new ArrayList<String[]>();
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            ResultSet r = s.executeQuery("SELECT A.nome, A.sobrenome, C.data, C.status, C.id FROM clientes A, conta B, pedido C WHERE A.id = B.idCliente AND B.idPedido = C.id");
            
            while(r.next()){
                String[] str = new String[7];
                str[0] = r.getString("C.id");
                str[1] = r.getString("A.nome");
                str[2] = r.getString("A.sobrenome");
                str[3] = r.getString("C.data");
                str[5] = r.getString("C.status");
                str[6] = r.getString("C.id");
                ret.add(str);
                
                int idPedido = r.getInt("C.id");
                
                
                String sql = "SELECT SUM(A.preco) FROM carrinho A WHERE A.idPedido = ? ;";
                PreparedStatement stm = connection.prepareStatement(sql);
                stm.setInt(1, idPedido);
                ResultSet r1 = stm.executeQuery();
                
                if(r1.first()){
                    str[4] = r1.getString("SUM(A.preco)");
                    if(str[4] == null){
                        str[4] = "0.00";
                    }
                }
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //seleciona os itens do carrinho de determinado pedido no banco de dados.
    public static List<String[]> listarCarrinho(int idPedido){
        List<String[]> ret = new ArrayList<String[]>();
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            String sql = "SELECT A.nome, B.nome, C.quantidade, C.preco, A.id FROM produtos A, categoria B, carrinho C WHERE C.idProduto = A.id AND A.idCategoria = B.id AND C.idPedido = ? ;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, idPedido);
            ResultSet r = stm.executeQuery();
            
            while(r.next()){
                String[] str = new String[5];
                str[0] = r.getString("A.nome");
                str[1] = r.getString("B.nome");
                str[2] = r.getString("C.quantidade");
                str[3] = r.getString("C.preco");
                str[4] = r.getString("A.id");
                ret.add(str);
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //Obtem o valor que já foi pago em determinado pedido no banco de dados.
    public static double valorPago(int idPedido){
        double ret = 0;
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            String sql = "SELECT SUM(nParcelas * valorParcela) FROM pagamento WHERE idPedido = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, idPedido);
            ResultSet r = stm.executeQuery();
            
            if(r.first()){
                ret = r.getDouble("SUM(nParcelas * valorParcela)");
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //Insere um pagamento no banco de dados.
    public static void pagar(int idPedido, int nParcelas, double valorParcela, String formaPagamento){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            String sql = "INSERT INTO pagamento VALUES ( null, ?, ?, ?, ? );";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, idPedido);
            stm.setInt(2, nParcelas);
            stm.setDouble(3, valorParcela);
            stm.setString(4, formaPagamento);
            stm.execute();
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
    }
    
    //Marca determinado pedido como pago no banco de dados.
    public static void setPago(int idPedido){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            String sql = "UPDATE pedido SET status = \"pago\" WHERE id = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, idPedido);
            stm.execute();
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
    }
    
    //Marca determinado pedido como cancelado no banco de dados.
    public static void setCancelado(int idPedido){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            String sql = "UPDATE pedido SET status = \"cancelado\" WHERE id = ?;";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, idPedido);
            stm.execute();
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
    }
    
    //Seleciona os pedidos de determinado cliente no banco de dados.
    public static List<String[]> listarPedidosCliente(int idCliente){
        List<String[]> ret = new ArrayList<String[]>();
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            
            String sql = "SELECT A.nome, A.sobrenome, C.data, C.status, C.id FROM clientes A, conta B, pedido C WHERE A.id = B.idCliente AND B.idPedido = C.id AND A.idUsuario = ?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setInt(1, idCliente);
            ResultSet r = stm.executeQuery();
            
            while(r.next()){
                String[] str = new String[7];
                str[0] = r.getString("C.id");
                str[1] = r.getString("A.nome");
                str[2] = r.getString("A.sobrenome");
                str[3] = r.getString("C.data");
                str[5] = r.getString("C.status");
                str[6] = r.getString("C.id");
                ret.add(str);
            }
            
            for(String[] ss : ret){
                int idPedido = new Integer(ss[6]);
                
                sql = "SELECT SUM(A.preco) FROM carrinho A WHERE A.idPedido = ? ;";
                PreparedStatement stm2 = connection.prepareStatement(sql);
                stm2.setInt(1, idPedido);
                ResultSet r1 = stm2.executeQuery();
                
                if(r1.first()){
                    ss[4] = r1.getString("SUM(A.preco)");
                    if(ss[4] == null){
                        ss[4] = "0.00";
                    }
                }
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //Conta quantos usuários existem no banco de dados.
    public static int countUsuarios(){
        int ret = -1;
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            ResultSet r = s.executeQuery("SELECT COUNT(*) FROM usuarios;");
            
            if(r.first()){
                ret = r.getInt("COUNT(*)");
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //Seleciona todos os pagamentos realizados no banco de dados.
    public static List<String[]> listarPagamento(){
        List<String[]> ret = new ArrayList<String[]>();
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            ResultSet r = s.executeQuery("SELECT * FROM pagamento");
            
            while(r.next()){
                String[] str = new String[4];
                str[0] = r.getString("idPedido");
                str[1] = r.getString("nParcelas");
                str[2] = r.getString("valorParcela");
                str[3] = r.getString("forma");
                ret.add(str);
            }
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
    
    //Método que gera estatisticas relevantes sobre os dados armazenados no banco de dados.
    public static List<String[]> listarEstatisticas(){
        List<String[]> ret = new ArrayList<String[]>();
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Banco de dados conectado!");
            Statement s = connection.createStatement();
            
            ResultSet r = s.executeQuery("SELECT T1.forma FROM (SELECT forma, COUNT(id) AS qtd FROM pagamento GROUP BY forma) AS T1 WHERE T1.qtd = (SELECT MAX(T2.qtd) FROM (SELECT forma, COUNT(id) AS qtd FROM pagamento GROUP BY forma) AS T2);");
            
            while(r.next()){
                String[] str = new String[2];
                str[0] = "Forma de pagamento mais utilizada:";
                str[1] = r.getString("T1.forma");
                ret.add(str);
            }
            
            r = s.executeQuery("SELECT B.nome, A.nome FROM produtos A, categoria B, (SELECT T2.idProduto FROM (SELECT T1.idProduto, COUNT(T1.idProduto) AS qtd FROM (SELECT B.idProduto FROM pedido A, carrinho B WHERE status != \"cancelado\" AND A.id = B.idPedido) AS T1 GROUP BY T1.idProduto) AS T2 WHERE T2.qtd IN  (SELECT MAX(qtd) FROM (SELECT T1.idProduto, COUNT(T1.idProduto) AS qtd FROM (SELECT B.idProduto FROM pedido A, carrinho B WHERE status != \"cancelado\" AND A.id = B.idPedido) AS T1 GROUP BY T1.idProduto) AS T2 )) AS T3 WHERE T3.idProduto = A.id AND A.idCategoria = B.id;");
            
            String[] str0 = new String[2];
            str0[0] = "Produto(s) mais vendido:";
            str0[1] = "";
            Boolean first = true;
             
            while(r.next()){
                if(first == false){
                    str0[1] += ", ";
                }
                str0[1] += r.getString("A.nome") + " da categoria " + r.getString("B.nome");
                first = false;
            }
            ret.add(str0);
            
            r = s.executeQuery("SELECT B.nome, A.nome FROM produtos A, categoria B, (SELECT T2.idProduto FROM (SELECT T1.idProduto, COUNT(T1.idProduto) AS qtd FROM (SELECT B.idProduto FROM pedido A, carrinho B WHERE status != \"cancelado\" AND A.id = B.idPedido) AS T1 GROUP BY T1.idProduto) AS T2 WHERE T2.qtd IN  (SELECT MIN(qtd) FROM (SELECT T1.idProduto, COUNT(T1.idProduto) AS qtd FROM (SELECT B.idProduto FROM pedido A, carrinho B WHERE status != \"cancelado\" AND A.id = B.idPedido) AS T1 GROUP BY T1.idProduto) AS T2 )) AS T3 WHERE T3.idProduto = A.id AND A.idCategoria = B.id;");
            
            String[] str2 = new String[2];
            str2[0] = "Produto(s) menos vendido:";
            str2[1] = "";
            first = true;
            
            while(r.next()){
                if(first == false){
                    str2[1] += ", ";
                }
                str2[1] += r.getString("A.nome") + " da categoria " + r.getString("B.nome");
                first = false;
            }
            ret.add(str2);
            
            r = s.executeQuery("SELECT A.nome, B.nome FROM produtos A, categoria B WHERE A.id IN (SELECT idProduto FROM item WHERE preco = (SELECT MAX(preco) FROM item)) AND A.idCategoria = B.id;;");
            
            String[] str1 = new String[2];
            str1[0] = "Produto(s) mais caro(s):";
            str1[1] = "";
            first = true;
            
            while(r.next()){
                if(first == false){
                    str1[1] += ", ";
                }
                str1[1] += r.getString("A.nome")+" ("  + r.getString("B.nome") + ")";
                first = false;
            }
            
            ret.add(str1);
            
            r = s.executeQuery("SELECT A.nome, B.nome FROM produtos A, categoria B WHERE A.id IN (SELECT idProduto FROM item WHERE preco = (SELECT MIN(preco) FROM item)) AND A.idCategoria = B.id;");
            
            str1 = new String[2];
            str1[0] = "Produto(s) mais barato(s):";
            str1[1] = "";
            first = true;
            
            while(r.next()){
                if(first == false){
                    str1[1] += ", ";
                }
                str1[1] += r.getString("A.nome")+" ("  + r.getString("B.nome") + ")";
                first = false;
            }
            
            ret.add(str1);
            
            
            r = s.executeQuery("SELECT A.nome, A.sobrenome FROM clientes A, conta B, (SELECT idPedido FROM (SELECT A.idPedido, SUM(A.preco) AS valor FROM carrinho A, pedido B WHERE A.idPedido = B.id AND B.status != \"cancelado\" GROUP BY idPedido) AS T1 WHERE valor IN (SELECT MAX(valor) FROM (SELECT A.idPedido, SUM(A.preco) AS valor FROM carrinho A, pedido B WHERE A.idPedido = B.id AND B.status != \"cancelado\" GROUP BY idPedido) AS T2)) AS C WHERE C.idPedido = B.idPedido AND B.idCliente = A.id;");
            
            str1 = new String[2];
            str1[0] = "Cliente(s) que gasta(m) mais:";
            str1[1] = "";
            first = true;
            
            while(r.next()){
                if(first == false){
                    str1[1] += ", ";
                }
                str1[1] += r.getString("A.nome") + " "  + r.getString("A.sobrenome");
                first = false;
            }
            
            ret.add(str1);
            
            r = s.executeQuery("SELECT A.nome, A.sobrenome FROM clientes A,  (SELECT idCliente FROM  (SELECT idCliente, COUNT(idPedido) AS qtd FROM conta GROUP BY idCliente) AS T1 WHERE qtd IN (SELECT MAX(qtd) FROM (SELECT idCliente, COUNT(idPedido) AS qtd FROM conta GROUP BY idCliente) AS T2)) AS B WHERE B.idCliente = A.id;");
            
            str1 = new String[2];
            str1[0] = "Cliente(s) que faz(em) mais pedidos";
            str1[1] = "";
            first = true;
            
            while(r.next()){
                if(first == false){
                    str1[1] += ", ";
                }
                str1[1] += r.getString("A.nome") + " "  + r.getString("A.sobrenome");
                first = false;
            }
            
            ret.add(str1);
            
            connection.close();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível se conectar ao banco de dados!", e);
        }
        
        return ret;
    }
}
