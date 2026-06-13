package pt.ipvc.kiosks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pt.ipvc.kiosks.bll.services.AuthService;
import pt.ipvc.kiosks.bll.services.OrderService;
import pt.ipvc.kiosks.bll.services.ProductService;
import pt.ipvc.kiosks.dal.entities.*;
import pt.ipvc.kiosks.dal.repository.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Aplicação principal — Sistema de Gestão de Quiosques Interativos
 * Projeto II — IPVC 2024/2025
 */
@SpringBootApplication
public class KiosksApplication {

    public static void main(String[] args) {
        SpringApplication.run(KiosksApplication.class, args);
    }

    @Bean
    CommandLineRunner demo(
            AuthService authService,
            ProductService productService,
            OrderService orderService,
            RoleRepository roleRepository,
            StoreRepository storeRepository,
            CategoryRepository categoryRepository,
            KioskRepository kioskRepository
    ) {
        return args -> {
            banner();

            // ── 0. SETUP ──────────────────────────────────────────────────────
            section("0. SETUP (dados pré-requisito)");

            titulo("0.1 Criar Roles");
            criarRoleSeNaoExiste(roleRepository, "ADMIN",    "Administrador do sistema");
            criarRoleSeNaoExiste(roleRepository, "MANAGER",  "Gestor de loja");
            criarRoleSeNaoExiste(roleRepository, "OPERATOR", "Operador de quiosque");

            titulo("0.2 Criar Loja");
            Store store = storeRepository.findByActiveTrue().stream()
                    .filter(s -> "Óptica Demo".equals(s.getStoreName())).findFirst()
                    .orElseGet(() -> storeRepository.save(new Store("Óptica Demo", "EYEWEAR", "Rua Principal, 1", "Viana do Castelo", "4900-000")));
            long storeId = store.getIdStore();
            ok("Loja: " + store + " (id=" + storeId + ")");

            titulo("0.3 Criar Categoria");
            Category category = categoryRepository.findByStoreIdStoreAndActiveTrue(storeId).stream()
                    .filter(c -> "Óculos de Sol".equals(c.getCategoryName())).findFirst()
                    .orElseGet(() -> categoryRepository.save(new Category("Óculos de Sol", 1, store)));
            long categoryId = category.getIdCategory();
            ok("Categoria: " + category + " (id=" + categoryId + ")");

            titulo("0.4 Criar Quiosque");
            Kiosk kiosk = kioskRepository.findByStoreIdStore(storeId).stream()
                    .filter(k -> "SN-DEMO-001".equals(k.getSerialNumber())).findFirst()
                    .orElseGet(() -> kioskRepository.save(new Kiosk("Quiosque A", "SN-DEMO-001", "TouchScreen Pro", store)));
            long kioskId = kiosk.getIdKiosk();
            ok("Quiosque: " + kiosk + " (id=" + kioskId + ")");

            // ── 1. AUTENTICAÇÃO ───────────────────────────────────────────────
            section("1. AUTENTICAÇÃO (AuthService)");

            titulo("1.1 Criar utilizador ADMIN");
            User admin = null;
            try {
                admin = authService.createUser("admin_demo", "segura123", "admin@kiosks.pt", "ADMIN");
                ok("Utilizador criado: " + admin);
            } catch (IllegalArgumentException e) {
                info("Utilizador já existe ou role inválida: " + e.getMessage());
            }

            titulo("1.2 Login com credenciais corretas");
            User user = authService.login("admin_demo", "segura123");
            if (user != null) {
                ok("Login bem-sucedido: " + user);
            } else {
                info("Login falhou (hash pode ser diferente de execução anterior)");
            }

            titulo("1.3 Login com password errada");
            User wrongLogin = authService.login("admin_demo", "errada");
            if (wrongLogin == null) ok("Login recusado corretamente para password errada");

            titulo("1.4 Verificar permissões");
            if (user != null) {
                ok("isAdmin:    " + authService.isAdmin(user));
                ok("isManager:  " + authService.isManager(user));
                ok("isOperator: " + authService.isOperator(user));
            }

            titulo("1.5 Criar utilizador OPERATOR");
            try {
                User op = authService.createUser("operador_demo", "operador123", "op@kiosks.pt", "OPERATOR");
                ok("Operador criado: " + op);
                ok("isAdmin(operador): " + authService.isAdmin(op));
            } catch (IllegalArgumentException e) {
                info(e.getMessage());
            }

            // ── 2. PRODUTOS ───────────────────────────────────────────────────
            section("2. PRODUTOS (ProductService)");

            titulo("2.1 Todos os produtos da Loja");
            List<Product> produtos = productService.getAllProductsByStore(storeId);
            if (produtos.isEmpty()) info("Nenhum produto na loja (ainda)");
            else produtos.forEach(p -> ok("  " + p));

            titulo("2.2 Produtos da Categoria");
            List<Product> porCategoria = productService.getProductsByCategory(categoryId);
            if (porCategoria.isEmpty()) info("Nenhum produto na categoria (ainda)");
            else porCategoria.forEach(p -> ok("  " + p));

            titulo("2.3 Pesquisar produtos com 'aviator'");
            List<Product> pesquisa = productService.searchProducts("aviator");
            if (pesquisa.isEmpty()) info("Nenhum produto encontrado");
            else pesquisa.forEach(p -> ok("  " + p));

            titulo("2.4 Criar novo produto");
            try {
                String skuUnico = "OKL-" + System.currentTimeMillis() % 100000;
                Product novo = productService.createProduct(
                        "Oakley Frogskins",
                        "Óculos de sol clássicos com armação acetato e lentes Prizm",
                        new BigDecimal("149.99"),
                        skuUnico, categoryId, storeId
                );
                ok("Produto criado: " + novo + " (id=" + novo.getIdProduct() + ")");

                titulo("2.5 Atualizar preço do produto criado");
                Product atualizado = productService.updateProduct(novo.getIdProduct(), null, null, new BigDecimal("159.99"));
                ok("Produto atualizado: " + atualizado);

                titulo("2.6 Desativar produto");
                productService.deactivateProduct(novo.getIdProduct());
                Product inativo = productService.getProductById(novo.getIdProduct());
                ok("Produto ativo=" + inativo.getActive() + " (desativado com sucesso)");

            } catch (IllegalArgumentException e) {
                info("Não foi possível criar produto: " + e.getMessage());
            }

            // ── 3. ENCOMENDAS ─────────────────────────────────────────────────
            section("3. ENCOMENDAS (OrderService)");

            titulo("3.0 Criar produto ativo para encomenda");
            Product prodEncomenda = null;
            try {
                String sku = "ENV-" + System.currentTimeMillis() % 100000;
                prodEncomenda = productService.createProduct(
                        "Ray-Ban Aviator", "Óculos aviador clássico",
                        new BigDecimal("129.99"), sku, categoryId, storeId
                );
                ok("Produto para encomenda criado: " + prodEncomenda + " (id=" + prodEncomenda.getIdProduct() + ")");
            } catch (IllegalArgumentException e) {
                info("Erro ao criar produto para encomenda: " + e.getMessage());
            }

            titulo("3.1 Criar encomenda no Quiosque");
            try {
                List<Product> ativos = productService.getAllProductsByStore(storeId);
                if (ativos.isEmpty()) {
                    info("Sem produtos ativos disponíveis");
                } else {
                    Map<Long, Integer> itens = new HashMap<>();
                    itens.put(ativos.get(0).getIdProduct(), 2);
                    if (ativos.size() > 1) itens.put(ativos.get(1).getIdProduct(), 1);

                    Order encomenda = orderService.createOrder(kioskId, itens);
                    ok("Encomenda criada: " + encomenda);
                    ok("  Referência: " + encomenda.getReference());
                    ok("  Total:      " + encomenda.getOrderTotal() + "€");
                    ok("  Estado:     " + encomenda.getStatus());

                    titulo("3.2 Buscar encomenda por referência");
                    Order porRef = orderService.findByReference(encomenda.getReference());
                    ok("Encontrada: " + porRef);

                    titulo("3.3 Atualizar estado para READY");
                    Order pronta = orderService.updateStatus(encomenda.getIdOrder(), "READY");
                    ok("Estado atualizado: " + pronta.getStatus());

                    titulo("3.4 Atualizar estado para COLLECTED");
                    Order levantada = orderService.updateStatus(encomenda.getIdOrder(), "COLLECTED");
                    ok("Estado atualizado: " + levantada.getStatus());

                    titulo("3.5 Tentar status inválido (deve rejeitar)");
                    try {
                        orderService.updateStatus(encomenda.getIdOrder(), "INVALIDO");
                    } catch (IllegalArgumentException e) {
                        ok("Exceção correta: " + e.getMessage());
                    }
                }
            } catch (IllegalArgumentException e) {
                info("Erro ao criar encomenda: " + e.getMessage());
            }

            titulo("3.6 Listar encomendas pendentes");
            List<Order> pendentes = orderService.getPendingOrders();
            ok("Encomendas PENDING: " + pendentes.size());
            pendentes.stream().limit(5).forEach(o -> ok("  " + o));

            titulo("3.7 Encomendas do Quiosque");
            List<Order> doQuiosque = orderService.getOrdersByKiosk(kioskId);
            ok("Total de encomendas no Quiosque: " + doQuiosque.size());
            doQuiosque.stream().limit(5).forEach(o -> ok("  " + o));
        };
    }

    private void criarRoleSeNaoExiste(RoleRepository roleRepository, String roleName, String description) {
        if (roleRepository.findByRoleName(roleName).isEmpty()) {
            roleRepository.save(new Role(roleName, description));
            ok("Role criada: " + roleName);
        } else {
            info("Role já existe: " + roleName);
        }
    }

    static void banner() {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║  Sistema de Gestão de Quiosques Interativos          ║");
        System.out.println("║  Demonstração BLL — Projeto II IPVC 2024/2025        ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println();
    }

    static void section(String title) {
        System.out.println("\n══════════════════════════════════════════════════════");
        System.out.println("  " + title);
        System.out.println("══════════════════════════════════════════════════════");
    }

    static void titulo(String t) { System.out.println("\n▶ " + t); }
    static void ok(String msg)   { System.out.println("  ✔ " + msg); }
    static void info(String msg) { System.out.println("  ℹ " + msg); }
}
