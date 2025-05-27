package com.banhoi.banhoi_api.controller;

import com.banhoi.banhoi_api.model.Cart;
import com.banhoi.banhoi_api.model.Order;
import com.banhoi.banhoi_api.model.Product;
import com.banhoi.banhoi_api.model.User;
import com.banhoi.banhoi_api.repository.CartRepository;
import com.banhoi.banhoi_api.service.CartService;
import com.banhoi.banhoi_api.service.OrderService;
import com.banhoi.banhoi_api.service.ProductService;
import com.banhoi.banhoi_api.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartRepository cartRepository;
    @GetMapping("/login")
    public String showLogin(Model model) {
        return "index";
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        try {
            User user = userService.loginUser(username, password);
            session.setAttribute("loggedInUser", user);
            response.put("success", "Đăng nhập thành công!");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestParam String username, @RequestParam String password, @RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        try {
            userService.registerUser(username, email, password, "USER");
            response.put("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            model.addAttribute("error", "Vui lòng đăng nhập để thêm vào giỏ hàng!");
            return "redirect:/login";
        }
        if (quantity <= 0) {
            model.addAttribute("error", "Số lượng không hợp lệ!");
            return "redirect:/cart";
        }
        Product product = productService.findById(productId).orElse(null);
        if (product == null) {
            model.addAttribute("error", "Sản phẩm không tồn tại!");
            return "redirect:/cart";
        }
        try {
            Cart cart = cartService.getOrCreateCart(user);
            cartService.addToCart(cart, product, quantity);
            model.addAttribute("success", "Thêm vào giỏ hàng thành công!");
            return "redirect:/cart";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi thêm vào giỏ hàng: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        System.out.println("ViewCart - Session ID: " + session.getId() + ", User: " +
                (user != null ? user.getUsername() : "null"));
        if (user == null) {
            System.out.println("No user found in session, redirecting to login");
            return "redirect";  // Chuyển về trang chủ thay vì login
        }
        try {
            Cart cart = cartService.getOrCreateCart(user);
            model.addAttribute("cart", cart);
            model.addAttribute("total", cartService.getCartTotal(cart));
            return "cart";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải giỏ hàng: " + e.getMessage());
            return "cart";
        }
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long productId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        try {
            Cart cart = cartService.getOrCreateCart(user);
            cartService.removeFromCart(cart, productId);
            model.addAttribute("success", "Đã xóa sản phẩm khỏi giỏ hàng!");
            return "redirect:/cart";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        try {
            Cart cart = cartService.getOrCreateCart(user);
            if (cart.getItems().isEmpty()) {
                model.addAttribute("error", "Giỏ hàng trống, không thể thanh toán!");
                return "cart";
            }
            double total = cartService.getCartTotal(cart);
            Order order = orderService.createOrder(user, cart.getItems(), total);
            cart.getItems().clear();
            cartRepository.save(cart);
            model.addAttribute("success", "Thanh toán thành công! Mã đơn hàng: " + order.getId());
            return "cart";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi thanh toán: " + e.getMessage());
            return "cart";
        }
    }
}