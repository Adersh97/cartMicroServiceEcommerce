package com.example.cartMicroService.cartMicroService.CartService;

import com.example.cartMicroService.cartMicroService.CartRepository.CartRepository;
import com.example.cartMicroService.cartMicroService.DTO.CartDTO;
import com.example.cartMicroService.cartMicroService.Entity.CartEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.swing.text.StyledEditorKit;
import java.util.Date;
import java.util.List;
import java.util.Properties;


@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CartServicImpl implements CartService{
    @Autowired
    CartRepository cartRepository;

    @Override
    @Transactional(readOnly = false)
    public int addCart(CartEntity cartEntity) {
        if(!cartEntityCheckNull(cartEntity))
        {
            return -1;
        }
        if(cartRepository.existsCartEntityByTokenAndProductIdAndMerchantId(cartEntity.getToken(),cartEntity.getProductId(),cartEntity.getMerchantId()))
        {
            CartEntity cartEntity1= cartRepository.findByTokenAndMerchantIdAndProductId(cartEntity.getToken(),cartEntity.getMerchantId(),cartEntity.getProductId());
            Integer tempQuantity=cartEntity.getQuantity()+cartEntity1.getQuantity();
            cartEntity1.setQuantity(tempQuantity);
            cartRepository.save(cartEntity1);
        }
        else
            cartRepository.save(cartEntity);

        return 0;
    }

    @Override
    public CartEntity getCart(String cartId) {
        CartEntity cartEntity= cartRepository.findOne(cartId);
        return cartEntity;

    }

    @Override
    public int updateCart(CartEntity cartEntity) {
        if(!cartEntityCheckNull(cartEntity))
        {
            return -1;
        }

        cartRepository.save(cartEntity);
        return 0;
    }

    @Override
    public int deleteCart(String cartId) {

        cartRepository.delete(cartId);
        return 0;
    }

    @Override
    public List<CartEntity> getByToken(String token) {
        return cartRepository.findAllByToken(token);


    }

    @Override
    public int deletetoken(String token) {
        cartRepository.deleteAllByToken(token);
        return 0;
    }

    @Override
    public int deleteProductId(String token,String productId){
        cartRepository.deleteByTokenAndProductId(token,productId);
        return 0;
    }

    @Override
    public CartEntity getByMerchantIdAndProductid(String merchantId, String productId) {
        return cartRepository.findByMerchantIdAndProductId(merchantId,productId);
    }

    @Override
    public Boolean existsCartEntityByTokenAndProductId(String token, String productId,String merchantId) {
       return cartRepository.existsCartEntityByTokenAndProductIdAndMerchantId(token,productId,merchantId);
    }

    @Override
    public CartEntity findByTokenAndMerchantIdAndProductId(String token,String merchantId,String productId) {
        return cartRepository.findByTokenAndMerchantIdAndProductId(token,merchantId,productId);
    }

    @Override
    public Double calculateSubtotal(String token) {
       return cartRepository.calculateSubTotal(token);
    }
    @Override
    public Boolean sendEmail(String toAddress,String message) throws MessagingException {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("noreplyfamazon@gmail.com", "ecommerce97");
                }
            });
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("noreplyfamazon@gmail.com", false));

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            msg.setSubject("E-commerce: Order Placed Successfully");
            try {
                msg.setContent(message, "text/html");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            msg.setSentDate(new Date());

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent("E-commerce Order Placed Successfully", "text/html");

            Transport.send(msg);


            return true;
        }catch (Exception e)
        {
            return  false;

        }
    }

    @Override
    public String getMessageBody(List<CartEntity> cartEntityList) {
        String message = "Order Sucessfully Placed <br> <br> <br> <br> <br> Order Details <br> ========================================== <br>  ";
        StringBuilder stringBuilder = new StringBuilder(message);
        for(CartEntity cartEntity:cartEntityList)
        {
            String body= "<br> Product Name:" + cartEntity.getProductName() + " </br> <br> Quantity: " + cartEntity.getQuantity() +"<br> <br> Price: "+ cartEntity.getQuantity()*cartEntity.getPrice() + "<br> <br> ------------------------------------------------------------------------ <br> ";
            stringBuilder.append(body);
        }
        String body = "<br> <br> <br> Thank You Placing Order! <br> <br> Visit us Again ";
        stringBuilder.append(body);
        String messageBody= stringBuilder.toString();
        return messageBody;

    }
    @Override
    public Boolean cartEntityCheckNull(CartEntity cartEntity)
    {
        if(cartEntity.getQuantity()==null||cartEntity.getPrice()==null||cartEntity.getProductName()==null||cartEntity.getToken()==null||cartEntity.getProductId()==null||cartEntity.getMerchantId()==null||cartEntity.getImageUrl()==null||cartEntity.getMerchantName()==null)
            return false;
        else
            return true;
    }


}
