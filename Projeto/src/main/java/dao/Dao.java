package dao;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.types.ObjectId;

public class Dao<T> {
    private final String URI = "mongodb://localhost:27017";
private final String DATABASE = "frota";
private final MongoClient mongoClient;
private final MongoDatabase database;
private final String colecao;
private final MongoCollection<T> collection;
private final Class<T> classe;

public Dao(Class<T> classe) {
    this.classe = classe;
    this.colecao = classe.getSimpleName().toLowerCase();
    System.out.println("Inicializando DAO para classe: " + classe.getSimpleName() + ", coleção: " + colecao);
    
    try {
        mongoClient = MongoClients.create(URI);
        CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.
                CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                        org.bson.codecs.configuration.CodecRegistries.
                                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        database = mongoClient.getDatabase(DATABASE).withCodecRegistry(pojoCodecRegistry);
        collection = database.getCollection(colecao, classe);
        System.out.println("Conexão com MongoDB estabelecida com sucesso.");
    } catch (Exception e) {
        System.err.println("Erro ao conectar ao MongoDB: " + e.getMessage());
        e.printStackTrace();
        throw e;
    }
}

public void alterar(String chave, String valor, T novo) {
    try {
        System.out.println("Alterando documento na coleção " + colecao + " onde " + chave + " = " + valor);
        
        if ("_id".equals(chave)) {
            ObjectId objectId = new ObjectId(valor);
            UpdateResult result = collection.replaceOne(new Document("_id", objectId), novo);
            System.out.println("Documento alterado: " + result.getModifiedCount() + " documento(s) modificado(s)");
        } else {
            UpdateResult result = collection.replaceOne(new Document(chave, valor), novo);
            System.out.println("Documento alterado: " + result.getModifiedCount() + " documento(s) modificado(s)");
        }
    } catch (Exception e) {
        System.err.println("Erro ao alterar documento: " + e.getMessage());
        e.printStackTrace();
        throw e;
    }
}

public boolean atualizarDevolucao(String placaVeiculo, String codigoMotorista, java.time.LocalDateTime dataHoraDevolucao) {
    if (!classe.equals(modelo.Utilidade.class)) {
        System.err.println("Erro: atualizarDevolucao só pode ser usado com a classe Utilidade");
        return false;
    }
    
    try {
        System.out.println("Atualizando devolução para veículo: " + placaVeiculo + ", motorista: " + codigoMotorista);
        
        Bson filtro = Filters.and(
            Filters.eq("placaVeiculo", placaVeiculo),
            Filters.eq("codigoMotorista", codigoMotorista),
            Filters.eq("dataHoraDevolucao", null)
        );
        
        Bson atualizacao = Updates.set("dataHoraDevolucao", dataHoraDevolucao);
        
        UpdateResult result = collection.updateOne(filtro, atualizacao);
        
        boolean atualizado = result.getModifiedCount() > 0;
        System.out.println("Devolução atualizada: " + atualizado + " (" + result.getModifiedCount() + " documento(s) modificado(s))");
        
        return atualizado;
    } catch (Exception e) {
        System.err.println("Erro ao atualizar devolução: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

public boolean excluir(String chave, String valor) {
    try {
        System.out.println("Excluindo documento da coleção " + colecao + " onde " + chave + " = " + valor);
        Document filter = new Document(chave, valor);
        DeleteResult result = collection.deleteOne(filter);
        boolean excluido = result.getDeletedCount() > 0;
        System.out.println("Documento excluído: " + excluido);
        return excluido;
    } catch (Exception e) {
        System.err.println("Erro ao excluir documento: " + e.getMessage());
        e.printStackTrace();
        throw e;
    }
}

public T buscarPorChave(String chave, String valor) {
    try {
        System.out.println("Buscando documento na coleção " + colecao + " onde " + chave + " = " + valor);
        T retorno = collection.find(new Document(chave, valor)).first();
        System.out.println("Documento encontrado: " + (retorno != null));
        return retorno;
    } catch (Exception e) {
        System.err.println("Erro ao buscar por chave: " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}

public List<T> buscarPorAtributo(String atributo, Object valor) {
    List<T> resultados = new ArrayList<>();
    try {
        System.out.println("Buscando por atributo: " + atributo + " = " + valor + " na coleção " + colecao);
        Bson filtro = Filters.eq(atributo, valor);
        
        try (MongoCursor<T> cursor = collection.find(filtro).iterator()) {
            while (cursor.hasNext()) {
                T objeto = cursor.next();
                resultados.add(objeto);
            }
        }
        System.out.println("Resultados encontrados: " + resultados.size());
        return resultados;
    } catch (Exception e) {
        System.err.println("Erro ao buscar por atributo: " + e.getMessage());
        e.printStackTrace();
        return resultados;
    }
}

public List<modelo.Utilidade> buscarRetiradasAtivas() {
    if (!classe.equals(modelo.Utilidade.class)) {
        System.err.println("Erro: buscarRetiradasAtivas só pode ser usado com a classe Utilidade");
        throw new IllegalStateException("Este método só pode ser usado com a classe Utilidade");
    }
    
    List<modelo.Utilidade> resultados = new ArrayList<>();
    try {
        System.out.println("Buscando retiradas ativas...");
        
        Bson filtro = Filters.eq("dataHoraDevolucao", null);
        
        try (MongoCursor<T> cursor = collection.find(filtro)
                .sort(new Document("dataHoraRetirada", -1))
                .iterator()) {
            
            while (cursor.hasNext()) {
                modelo.Utilidade utilidade = (modelo.Utilidade) cursor.next();
                
                if (utilidade.getPlacaVeiculo() != null && 
                    utilidade.getCodigoMotorista() != null &&
                    utilidade.getDataHoraRetirada() != null) {
                    
                    resultados.add(utilidade);
                    System.out.println("Retirada ativa encontrada: " + 
                        utilidade.getPlacaVeiculo() + " - " + 
                        utilidade.getCodigoMotorista() + " - " +
                        utilidade.getDataHoraRetirada());
                }
            }
        }
        
        System.out.println("Retiradas ativas encontradas: " + resultados.size());
        return resultados;
    } catch (Exception e) {
        System.err.println("Erro ao buscar retiradas ativas: " + e.getMessage());
        e.printStackTrace();
        return resultados;
    }
}

public List<modelo.Utilidade> buscarRetiradasAtivasPorVeiculo(String placaVeiculo) {
    if (!classe.equals(modelo.Utilidade.class)) {
        System.err.println("Erro: buscarRetiradasAtivasPorVeiculo só pode ser usado com a classe Utilidade");
        throw new IllegalStateException("Este método só pode ser usado com a classe Utilidade");
    }
    
    List<modelo.Utilidade> resultados = new ArrayList<>();
    try {
        System.out.println("Buscando retiradas ativas para veículo: " + placaVeiculo);
        
        Bson filtro = Filters.and(
            Filters.eq("placaVeiculo", placaVeiculo),
            Filters.eq("dataHoraDevolucao", null)
        );
        
        try (MongoCursor<T> cursor = collection.find(filtro).iterator()) {
            while (cursor.hasNext()) {
                resultados.add((modelo.Utilidade) cursor.next());
            }
        }
        
        System.out.println("Retiradas ativas encontradas para veículo " + placaVeiculo + ": " + resultados.size());
        return resultados;
    } catch (Exception e) {
        System.err.println("Erro ao buscar retiradas ativas por veículo: " + e.getMessage());
        e.printStackTrace();
        return resultados;
    }
}

public List<modelo.Utilidade> buscarRetiradasAtivasPorMotorista(String codigoMotorista) {
    if (!classe.equals(modelo.Utilidade.class)) {
        System.err.println("Erro: buscarRetiradasAtivasPorMotorista só pode ser usado com a classe Utilidade");
        throw new IllegalStateException("Este método só pode ser usado com a classe Utilidade");
    }
    
    List<modelo.Utilidade> resultados = new ArrayList<>();
    try {
        System.out.println("Buscando retiradas ativas para motorista: " + codigoMotorista);
        
        Bson filtro = Filters.and(
            Filters.eq("codigoMotorista", codigoMotorista),
            Filters.eq("dataHoraDevolucao", null)
        );
        
        try (MongoCursor<T> cursor = collection.find(filtro).iterator()) {
            while (cursor.hasNext()) {
                resultados.add((modelo.Utilidade) cursor.next());
            }
        }
        
        System.out.println("Retiradas ativas encontradas para motorista " + codigoMotorista + ": " + resultados.size());
        return resultados;
    } catch (Exception e) {
        System.err.println("Erro ao buscar retiradas ativas por motorista: " + e.getMessage());
        e.printStackTrace();
        return resultados;
    }
}

public void inserir(T objeto) {
    try {
        System.out.println("Inserindo objeto na coleção " + colecao);
        System.out.println("Objeto: " + objeto.toString());
        collection.insertOne(objeto);
        System.out.println("Objeto inserido com sucesso.");
    } catch (Exception e) {
        System.err.println("Erro ao inserir objeto: " + e.getMessage());
        e.printStackTrace();
        throw e;
    }
}

public List<T> listarTodos() {
    ArrayList<T> todos = new ArrayList<>();
    try {
        System.out.println("Listando todos os objetos da coleção " + colecao);
        try (MongoCursor<T> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                T elemento = cursor.next();
                todos.add(elemento);
            }
        }
        System.out.println("Total de objetos encontrados: " + todos.size());
        return todos;
    } catch (Exception e) {
        System.err.println("Erro ao listar todos os objetos: " + e.getMessage());
        e.printStackTrace();
        return todos;
    }
}

public void fecharConexao() {
    try {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Conexão com MongoDB fechada.");
        }
    } catch (Exception e) {
        System.err.println("Erro ao fechar conexão: " + e.getMessage());
        e.printStackTrace();
    }
}
}