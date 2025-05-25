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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartRepository cartRepository;

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            Cart cart = cartService.getOrCreateCart(user);
            model.addAttribute("cart", cart);
        }
        return "index";
    }

    @GetMapping("/login")
    public String showLogin(Model model, @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Tên người dùng hoặc mật khẩu không đúng!");
        }
        User user = (User) model.getAttribute("loggedInUser"); // Lấy từ session
        if (user != null) {
            Cart cart = cartService.getOrCreateCart(user);
            model.addAttribute("cart", cart);
        }
        return "index"; // Trả về trang index với modal
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        try {
            User user = userService.loginUser(username, password);
            session.setAttribute("loggedInUser", user); // Lưu user vào session
            Cart cart = cartService.getOrCreateCart(user);
            model.addAttribute("cart", cart);
            return "redirect:/"; // Chuyển hướng về trang chủ
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/login?error=true"; // Chuyển hướng lại trang login với lỗi
        }
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String email, @RequestParam String password, Model model) {
        try {
            userService.registerUser(username, email, password, "USER");
            model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login?success=true"; // Chuyển hướng về login với thông báo thành công
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/login?error=true"; // Chuyển hướng về login với lỗi
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Hủy session
        return "redirect:/"; // Chuyển hướng về trang chủ
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long productId, @RequestParam(defaultValue = "1") int quantity, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            model.addAttribute("error", "Vui lòng đăng nhập để thêm vào giỏ hàng!");
            return "redirect:/login?error=true";
        }
        Product product = productService.findById(productId).orElse(null);
        if (product == null || product.getStock() < quantity) {
            model.addAttribute("error", "Sản phẩm không tồn tại hoặc không đủ số lượng!");
            return "redirect:/products/" + productId;
        }
        Cart cart = cartService.getOrCreateCart(user);
        cartService.addToCart(cart, product, quantity);
        model.addAttribute("success", "Thêm vào giỏ hàng thành công!");
        return "redirect:/products/" + productId; // Quay lại trang chi tiết sản phẩm
    }

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            model.addAttribute("error", "Vui lòng đăng nhập để xem giỏ hàng!");
            return "redirect:/login?error=true";
        }
        Cart cart = cartService.getOrCreateCart(user);
        model.addAttribute("cart", cart);
        model.addAttribute("total", cartService.getCartTotal(cart));
        return "cart";
    }

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        Cart cart = cartService.getOrCreateCart(user);
        double total = cartService.getCartTotal(cart);
        Order order = orderService.createOrder(user, cart.getItems(), total);
        cart.getItems().clear();
        cartRepository.save(cart);
        model.addAttribute("success", "Thanh toán giả lập thành công! Mã đơn hàng: " + order.getId());
        return "cart"; // Quay lại trang giỏ hàng để hiển thị thông báo
    }
}