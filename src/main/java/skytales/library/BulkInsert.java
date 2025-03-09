//package skytales.library;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch.core.BulkRequest;
//import co.elastic.clients.elasticsearch.core.BulkResponse;
//import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
//import org.springframework.web.bind.annotation.*;
//import skytales.library.dto.BookData;
//import skytales.library.service.BookService;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/books")
//public class BulkInsert {
//
//    private final ElasticsearchClient elasticsearchClient;
//    private final BookService bookService;
//
//
//
//    public BulkInsert(ElasticsearchClient elasticsearchClient, BookService bookService) {
//        this.elasticsearchClient = elasticsearchClient;
//        this.bookService = bookService;
//    }
//
//    @PostMapping("/bulk-create")
//    public String bulkCreateBooks() throws IOException {
//
//
//        List<BookData> books = List.of(
//                new BookData("Moonlight Sorcery", "Epic Fantasy", "Elena Nightshade", "https://i.etsystatic.com/26410296/r/il/d01963/5698879945/il_570xN.5698879945_3cv3.jpg", "https://images.squarespace-cdn.com/content/v1/600f53df6203760523747395/1623433821373-UTVPOR93MGIY3Q1VFX53/lunar-magic.jpg?format=1000w", "2021", "19", "200", "In a world where lunar magic shapes destinies, a young mage embarks on a perilous quest to uncover a secret that could alter the course of history. Mystical landscapes, magical creatures, and fierce battles await."),
//                new BookData("The Raven’s Secret", "Dark Mystery", "Samuel Drake", "https://images-platform.99static.com//kDJ4dnhYp3peFGj_fN2mmibONGk=/960x21:3467x2528/fit-in/500x500/projects-files/68/6824/682496/e15a2c95-803b-4b5f-8992-67600f14dba6.jpg", "https://www.ikaruna.eu/wp-content/uploads/2020/07/Ravens-spread-copy.jpg", "2020", "15", "100", "A young ornithologist uncovers a society of intelligent ravens with the power to control fate. As dark secrets unfold, one mystery leads to another, blending intrigue with the supernatural."),
//                new BookData("Dragons of the Twilight Realm", "High Fantasy", "Adrian Blaze", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTB-r0LN0QLfxF6h4AOTjl0atqvAoBaFPjljA&s", "https://thenobleartist.com/wp-content/uploads/2022/01/book1-sketch2_col3.jpg", "2022", "22", "27", "A dragon-wielding warrior is called to stop an ancient evil from resurrecting. In the twilight realm, dark forces prepare to unleash terror, and the hero must race against time."),
//                new BookData("Stormbreaker", "Post-Apocalyptic Thriller", "Jeffrey Black", "https://c4.wallpaperflare.com/wallpaper/460/140/13/sci-fi-futuristic-city-dark-theme-fantasy-wallpaper-preview.jpg", "https://images.squarespace-cdn.com/content/v1/64b0cf5d09a7db5a37783cf5/e62bd151-a531-4b88-bd3d-560197d2e418/ageofstorms_epicfantasy_jeffbrown.jpg", "2023", "25","23", "The world is in turmoil as violent storms ravage the land. A weather mage must unlock the power of the tempest to save humanity. Action-packed with supernatural abilities and explosive tension."),
//                new BookData("The Guardians of the Forest", "Mystical Fantasy", "Sophia Rivers", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRAi43DeO2FPQENdfxny6yRU5nOKZRs27TY9w&s", "https://i.pinimg.com/736x/1e/db/06/1edb06ac3427ddedcdd622a511a8a402.jpg", "2021", "18","350", "A young girl with a gift to communicate with nature discovers the forest’s deepest secrets, where ravens guard ancient wisdom. In this enchanted world, magic is alive, and dangers lurk in every shadow."),
//                new BookData("Whispers in the Dark", "Gothic Fantasy", "M. Blackthorn", "https://www.shutterstock.com/image-vector/mathematics-vector-cover-background-scientific-260nw-320996975.jpg", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS7sbYsqXK67e3r50RVJaMuE46x9ssvxqIlpQ&s", "2022", "20", "400","A world consumed by dark magic and monstrous creatures, where the protagonist must uncover lost relics to challenge a powerful sorcerer. A chilling tale that lures you into its eerie embrace."),
//                new BookData("The Raven Chronicles", "Supernatural Thriller", "Catherine Moore", "https://thumbs.dreamstime.com/b/finger-print-investigation-2882107.jpg", "https://mir-s3-cdn-cf.behance.net/project_modules/max_1200/561b1483830519.5d48f1ff93014.jpg", "2020", "17","200", "When a detective stumbles upon a hidden world of ravens that possess unimaginable abilities, they must confront powerful forces that threaten not only the world of man but also that of myth."),
//                new BookData("Realm of Echoes", "Epic Fantasy", "Isla Dorne", "https://c4.wallpaperflare.com/wallpaper/800/864/33/pc-game-ps4-hellblade-wallpaper-preview.jpg", "https://m.media-amazon.com/images/I/81cquiyLW8L._AC_UF894,1000_QL80_.jpg", "2022", "23","117", "A hero's journey into the heart of a mystical land, filled with dragons, forgotten gods, and sinister prophecies. As ancient powers awaken, the protagonist is forced to rise against impossible odds."),
//                new BookData("Whispers of the Moonlit Forest", "Magical Realism", "Ariana Frost", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRAi43DeO2FPQENdfxny6yRU5nOKZRs27TY9w&s", "https://assets.hellovector.com/product-images/b_5341.jpg", "2021", "21", "154", "In a forgotten corner of the world, magical creatures live among the trees, but when a new threat emerges, a young girl must use her powers to communicate with them and protect their home."),
//                new BookData("The Sorcerer's Legacy", "Dark Fantasy", "Zara Night", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ2ck2kcF4bON6QCO21WWtOsAxPs_NZbqZ-KYy_yJRd4ab5jHa1NQezspXi6A2lCF21Hng&usqp=CAU", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRm1UVXwo559Bh8RBJo6TdAYhmaNXY7cFgPqvqlPfv_OWFpUM0F37sxHQZSTfY8dTBQHmU&usqp=CAU", "2022", "19", "646","A young sorcerer discovers that their bloodline is entwined with a cursed legacy. A world of dark magic and forgotten gods beckons, where survival requires more than just power—it requires mastery of the unknown."),
//                new BookData("Enigma of the Shadows", "Supernatural Mystery", "Elijah Crowe", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRAi43DeO2FPQENdfxny6yRU5nOKZRs27TY9w&s", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcROFgZK4SHdaApFn7XZThw61t2Mv4EHOcN1nA&s", "2020", "17","187","A detective is pulled into a web of intrigue when an ancient secret society, hidden for centuries, is discovered. As dark forces rise, they must navigate a world where mystery and danger intertwine."),
//                new BookData("Journey Beyond the Stars", "Space Fantasy", "Marcus Grey", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQhztoH1finwpmgEPeZB0W5awkYqDiBiLWovA&s", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRJZfzxmNFnJeoAPOgG9T-yMZ1o12_kELxxrA&s", "2021", "20", "467", "In an age where starships journey across the galaxy, one hero must navigate cosmic storms, alien encounters, and ancient prophecies to uncover the secrets of a forgotten race."),
//                new BookData("The Chronicles of the Abyss", "Cosmic Horror", "Vincent Black", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQhztoH1finwpmgEPeZB0W5awkYqDiBiLWovA&s", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRiTJsv4GEqEguc3ZbenVD3Vq1XtZXJJB2i8A&s", "2022", "18","435", "The dark reaches of space hold unspeakable horrors. When a scientist discovers a portal to an unknown dimension, they awaken an ancient terror that threatens to consume the universe."),
//                new BookData("Shadows of the Past", "Gothic Thriller", "Nina Morrow", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQhztoH1finwpmgEPeZB0W5awkYqDiBiLWovA&s", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQTEc37-rtROLkKnWlZnDwLAMXU1tD5mIGo0a1HqEHpucnZ3Qt3Ixdi3sqhPFPb35eRAas&usqp=CAU", "2020", "19", "345","A detective delves into the shadows of a dark, mysterious past, uncovering a secret that could bring an entire city to its knees."),
//                new BookData("The Midnight Gambit", "Thriller", "Lucinda Sterling", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQCwxr_ik2-fp_hcdhztRBV83b1Jhinh8EHC-enfbr_mQqPa0o65vXQKzhkDZw6t7qBBMc&usqp=CAU", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR7PsdYwYAVWOM_6Dj8dMdG_qA6cg1KrU7a3lqFM3sEQ-u4niTszOdifhxVk1Lsz3RxMyM&usqp=CAU", "2020", "17", "345","A high-stakes poker game turns deadly when the stakes are more than just money—lives are on the line. As one player gets drawn into a dangerous game of manipulation and betrayal, they must navigate through a maze of lies and deception to uncover the real objective behind the game. With time running out and no one to trust, the only way out may be through the game itself. Will they survive, or will they become part of the deadly wager?"),
//                new BookData("Frostbite", "Sci-Fi Thriller", "Dylan Hurst", "https://covers.bookcoverzone.com/slir/w450/png24-front/bookcover0028631.jpg", "https://covers.bookcoverzone.com/slir/w450/png24-front/bookcover0008065.jpg", "2024", "23","572", "In a dystopian future where Earth has entered a new ice age, survival depends on the ability to adapt. A group of scientists discovers a way to reverse the freezing process, but their discovery comes with a deadly price. As they race against time to save humanity, they must face the challenges of a frozen world and a growing conspiracy that threatens their every move. The cold is their greatest enemy, but betrayal may be even worse."),
//                new BookData("The Last Light", "Mystery", "Adelaide Storm", "https://covers.bookcoverzone.com/slir/w450/png24-front/bookcover0031766.jpg", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSpYwpN4ryKanfu6Rk1nROMR2Kpfsqi4etWXA&s", "2022", "18", "581","When a renowned detective is found dead under mysterious circumstances, his protege takes on the case. As she dives deeper into his past, she uncovers connections to a series of unsolved crimes. Each clue brings her closer to a dangerous truth, but the closer she gets, the more powerful figures threaten her life. Will she unravel the mystery in time, or will she fall victim to the same shadow that claimed her mentor? A chilling tale of suspense and deception."),
//                new BookData("Tides of Destiny", "Historical Fantasy", "Eleanor Cole", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT8dFUGU78zJnafmxoCl6fIwRMQ8CUj2spOTjliiuk1juOFM0ppxCV9lfqmhbxppVn9mqU&usqp=CAU", "https://sun9-57.userapi.com/s/v1/ig2/oXIbRbW6a-94VJkFLXv0m4ndwfmA395xONp_jbO7YMm5KyKi31AlabD4dg-PjTAEKau2cHixj7W39xopDVtiP3aZ.jpg?quality=95&as=32x48,48x73,72x109,108x163,160x242,240x363,360x544,480x725,540x816,640x967,720x1088,1080x1632,1280x1934,1440x2175,1688x2550&from=bu&u=MeWTIWi0O0D_cKm0_7yonXutf_4DyuhsQ8CxX_mOLNM&cs=534x807", "2023", "22", "317","A sailor turned king embarks on a perilous journey to reclaim his stolen throne. In a world where the tides rule, his quest leads him through enchanted seas and forgotten lands, where ancient magic is the key to his success. But the deeper he dives into the mysteries of the past, the more he discovers that his enemies are not only human. This captivating historical fantasy explores love, betrayal, and the power of destiny."),
//                new BookData("Bound by Blood", "Horror", "Jonah Creed", "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/romance-novel-book-cover-template-%281%29-design-81dcd287a2ecaf722e864279836bae9b_screen.jpg?ts=1711679433", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTrSSMX7dmq_tkR4hydmYFsja4wjzykTcV2MZmrKTMfZ9vtEKHa51PH-UPP8sh-Hdi41uY&usqp=CAU", "2022", "26", "456","A small town is haunted by a series of gruesome murders that seem to follow a dark family lineage. As the investigation unfolds, the town's most prominent families are linked to the crimes, and the detectives find themselves caught in a deadly game of cat and mouse. With each clue that leads them closer to the truth, they must confront their darkest fears. Will they stop the killings before it's too late, or will they become the next victims of the blood-stained past?"),
//                new BookData("Shadows of the Past", "Gothic Thriller", "Nina Morrow", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQhztoH1finwpmgEPeZB0W5awkYqDiBiLWovA&s", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQTEc37-rtROLkKnWlZnDwLAMXU1tD5mIGo0a1HqEHpucnZ3Qt3Ixdi3sqhPFPb35eRAas&usqp=CAU", "2020", "19","456", "A detective delves into the shadows of a dark, mysterious past, uncovering a secret that could bring an entire city to its knees. As he investigates, he stumbles upon a conspiracy that stretches far beyond the city's borders. As the body count rises, he realizes that the shadows hold far more than he expected, and the cost of uncovering the truth may be more than he's willing to pay. Will he survive the shadows, or will they consume him entirely?")
//        );
//
//
//        books.forEach(bookService::createBook);
//
//        BulkRequest bulkRequest = BulkRequest.of(b -> b
//                .index("library")
//                .operations(books.stream()
//                        .map(book -> BulkOperation.of(op -> op
//                                .index(idx -> idx.document(book))))
//                        .collect(Collectors.toList()))
//        );
//
//        BulkResponse response = elasticsearchClient.bulk(bulkRequest);
//        return response.errors() ? "Bulk index failed!" : "Books indexed successfully!";
//    }
//}
//
