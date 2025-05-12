package com.monocept.chatbot.utils;

import com.monocept.chatbot.controller.MessageController;
import com.monocept.chatbot.enums.BotCommunicationFlow;
import com.monocept.chatbot.enums.MessageTo;
import com.monocept.chatbot.model.dto.MediaDto;
import com.monocept.chatbot.model.request.ReceiveMessageRequest;
import com.monocept.chatbot.model.request.SendMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class BotUtility {




    @Async("taskExecutor")
    public  ReceiveMessageRequest getBotResponse(SendMessageRequest request){
        Map<String, String> message = new LinkedHashMap<>();

        message.put("hi", "Hello! 👋 Welcome to MaxSecure Insurance Bot.\nHow can I assist you today?\n1. 📜 View Policy Details\n2. 💰 Check Premium Due\n3. 🧾 Claim Status\n4. 👨‍💼 Talk to Advisor");

        message.put("1", "Sure! Please enter your Policy Number.");

        message.put("pol123456789", "Here are your policy details:\n- Policy Type: Life Insurance\n- Start Date: 12-Jan-2023\n- Status: ✅ Active\n- Premium Due: ₹10,000 on 12-Jan-2025");

        message.put("check premium", "Your next premium of ₹10,000 is due on 12-Jan-2025. Would you like to:\n1. 💳 Pay Now\n2. 🔔 Set Reminder");

        message.put("pay now", "Redirecting to payment gateway... 💳");

        message.put("set reminder", "Reminder set! We will notify you 7 days before the due date. ✅");

        message.put("2", "Please provide your Policy Number to check the premium.");

        message.put("claim", "Sure! Please provide your claim reference number.");

        message.put("clm987654", "Claim Status:\n- Claim ID: CLM987654\n- Status: ✅ Approved\n- Amount: ₹25,000\n- Processed Date: 03-Mar-2025");

        message.put("3", "Please enter your Claim ID to fetch claim status.");

        message.put("4", "Connecting you to our insurance advisor... 👨‍💼 Please wait.");

        message.put("talk to agent", "You're now being connected to a live representative. 💬");

        message.put("thanks", "You're welcome! 😊 Anything else I can help you with?");

        message.put("no", "Thank you for using MaxSecure Insurance Bot. Have a great day! 👋");

        message.put("default", "I'm sorry, I didn't quite catch that. Please choose an option from the menu.");

        message.put("hr related", "You can ask HR related queries such as leave balance, holidays, or company policies.");

        message.put("policy related", "You can check your insurance policy, update details, or renew it.");

        String userMessage = request.getText().toLowerCase();
        Map<String, String> media = null;
        if(!userMessage.equalsIgnoreCase("hi")){
            media = selectMedia();
        }
        String botMessage  = message.get(userMessage);

        return  createSendMessageObject(request , botMessage, media);

    }

    public ReceiveMessageRequest createSendMessageObject(SendMessageRequest sendMessageRequest , String botMessage, Map<String, String> media){

                String table = "`\n" +
                        "| Band | Fuel (Per Annum) | Car Driver (Per Annum) | Car Maintenance (Per Annum) |\n" +
                        "|------|------------------|------------------------|-----------------------------|\n" +
                        "| 0/UC | At actual        | At actual              | At actual                   |\n" +
                        "| 1    | At actual        | At actual              | At actual                   |\n" +
                        "| 2A   | At actual        | At actual              | At actual                   |\n" +
                        "| 2    | At actual        | At actual              | At actual                   |\n" +
                        "| 3B   | ₹170,000         | ₹180,000               | ₹100,000                    |\n" +
                        "| 3A   | ₹170,000         | ₹180,000               | ₹100,000                    |\n" +
                        "| 3    | ₹170,000         | ₹180,000               | ₹100,000                    |\n" +
                        "| 4    | ₹100,000         | Not applicable         | ₹80,000                     |\n" +
                        "| 5    | ₹75,000          | Not applicable         | ₹70,000                     |\n" +
                        "`";
                Set<Integer> set = Set.of(0, 2, 4);
                int num = getRandomBetween0And5();
                if(!set.contains(num)){
                    table = null;
                }

                List<String> mediaList = new ArrayList<>();
                List<String> imageList = new ArrayList<>();
                List<String> videoList = new ArrayList<>();
                List<String> documentList = new ArrayList<>();
                if(media != null){
//                    String value = getMedia(media);
//                    log.info("createSendMessageObject : Value: " + value);
                    Set<Map.Entry<String, String>> keys = media.entrySet();
                    for(Map.Entry<String, String> entry : keys){
                        if (entry.getKey().equalsIgnoreCase("image")){
                            imageList.add(entry.getValue());
                        } else if(entry.getKey().equalsIgnoreCase("video")){
                            videoList.add(entry.getValue());
                        } else {
                            documentList.add(entry.getValue());
                        }
                    }

                }

                // Sample media details
                MediaDto.MediaDetail imageDetail = new MediaDto.MediaDetail("jpg", imageList);
                MediaDto.MediaDetail videoDetail = new MediaDto.MediaDetail("mp4", videoList);
                MediaDto.MediaDetail docDetail = new MediaDto.MediaDetail("pdf", documentList);

                // MediaDto using builder
                MediaDto mediaDto = MediaDto.builder()
                        .image(Arrays.asList(imageDetail))
                        .video(Arrays.asList(videoDetail))
                        .document(Arrays.asList(docDetail))
                        .build();

                // Acknowledgement using builder
                ReceiveMessageRequest.Acknowledgement ack = ReceiveMessageRequest.Acknowledgement.builder()
                        .status("RECEIVED")
                        .build();

                // Message using builder
                ReceiveMessageRequest.Message message = ReceiveMessageRequest.Message.builder()
                        .text(botMessage)
                        .table(table)
                        .botOption(true)
                        .media(mediaDto)
                        .acknowledgement(ack)
                        .build();

                // Entry using builder
                ReceiveMessageRequest.Entry entry = ReceiveMessageRequest.Entry.builder()
                        .messageTo(MessageTo.USER)
                        .replyToMessageId("")
                        .message(message)
                        .build();

                // Final ReceiveMessageRequest using builder
                ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                        .emailId(sendMessageRequest.getMessageId())
                        .userId(sendMessageRequest.getUserId())
                        .status(BotCommunicationFlow.COACH)
                        .platform("MSPACE")
                        .isConversationEnded(false)
                        .entry(entry)
                        .build();

                return  request;

            }


    public static int getRandomBetween0And2() {
        Random random = new Random();
        return random.nextInt(3); // generates 0, 1, or 2
    }

    public static int getRandomBetween0And5() {
        Random random = new Random();
        return random.nextInt(5); // generates 0, 1, or 2
    }

    public String getMediaData(String media, int number){
        List<String> image = List.of("https://media.slidesgo.com/storage/54799/upload.png",
                "https://static.vecteezy.com/system/resources/previews/007/432/016/non_2x/renew-word-on-white-keyboard-free-photo.jpg",
                        "https://picsum.photos/200");

        List<String> video = List.of("https://www.pexels.com/video/person-writing-on-a-board-7164232/" ,
                "https://www.pexels.com/video/a-person-explaining-a-document-7821854/",
                "https://www.pexels.com/video/an-agent-talking-to-his-client-while-holding-documents-7822017/");

        List<String> document = List.of("https://www.iii.org/sites/default/files/docs/pdf/Insurance_Handbook_20103.pdf",
                "https://www.sigc.edu/sigc/department/commerce/studymet/Insurance.pdf",
                "https://www.mikerussonline.com/CA/pdfs/1.pdf");

        switch (media)
        {
            case "document":
                return document.get(number);
            case "video":
                return video.get(number);
            case "image":
                return image.get(number);
            default:
              return "";
        }

    }

    public Map<String, String> selectMedia(){
        List<String> list = List.of("document", "video", "image");
        int number = getRandomBetween0And2();
        String media =  list.get(number);
        Map<String, String> map = new HashMap<>();
        map.put(media, getMediaData(media, number));
        return map;
    }

    public String getMedia(Map<String, String> media){
        List<String> list = List.of("document", "video", "image");
        log.info("getMedia: start " + media);
        for(String l:list){
            String m = media.get(l);
            log.info("getMedia: value of l " + l);
            log.info("getMedia: value of m " + m);
            if(m != null) return m;
        }
        return list.get(getRandomBetween0And2());
    }


}
