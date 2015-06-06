(ns solari.model
  (:require [liberator.core :refer [defresource resource request-method-in]]
            [environ.core :refer [env]]
            [clojure.core.async :refer [>! <! >!! <!! go chan buffer close! thread alts! alts!!]]
            [clojure.string :as string]
            [solari.projects :as pr]
            [clojure.java.jdbc :as sql]
            [clojure.java.jdbc.deprecated :as sql-old]))

(def db
  (env
    :heroku-postgresql-rose-url
    "postgresql://root:1fishy4me@localhost:5432/solari"))

(def home-page-atom
  (atom {:bold "Come on in. We're Solari architects."
         :paragraph " Our studio is based in Wellington and our thoughts, projects and experiences span New Zealand, Australia and beyond. When working with you we focus on speaking a common language  - you’ll find no architectural lingo here. We take your vision from pictures, words, half-formed or full-formed ideas and “ya knows” and translate them into architecture representative of your values, goals and personality. Our strengths lie in commercial, residential and multi-residential projects where we work on the small and the large. We’re flexible, agile and updateable but we do keep one thing consistent across the board; every project is led by YOUR vision and crafted by our tools and expertise."}))

(def residential-atom
  (atom {:bold "Homes are personal projects - we like that."
         :paragraph " When designing a residential project we take on the thoughts, feelings, personality and unique circumstances of the client (that’s you). We work closely with you to ensure that your home is exactly that – yours. You’re with us every step of the way. This not only makes absolute sense but it undoubtedly delivers the best results. We share the challenges and successes with you. And make you the expert of your own project by going at a pace that promotes attention to detail and clarity of thought from start to finish."}))

(def multi-unit-atom
  (atom {:bold "Solari’s success in the multi-unit residential development sector across New Zealand and Australia comes down to what we like to call ‘The Solari Way’."
         :paragraph " In a nutshell it’s an approach that balances the values and objectives of the developer, designer and tenants. Everyone involved with the project stands to benefit from such an insightful approach. Blending our understanding of commercial realities, how a target market perceives quality living spaces and how to effectively manage the design process from sketches to site, ensures our developments maintain their purpose and quality."}))

(def commercial-atom
  (atom {:bold "At Solari we don’t define commercial buildings by their sheer scale but by their purpose. "
         :paragraph " We treat them as strategic assets, marketing tools and enablers of achieving business goals. We take full advantage of the power commercial and workplace design has to impact three key objectives shared by all businesses (including our own): efficiency, effectiveness and expression. "}))

(def for-you-atom
  (atom {:bold "When working with you we focus on speaking a common language."
         :paragraph " We take your vision from pictures, words, half-formed or full-formed ideas and “ya knows” and translate them into architecture representative of your values, goals and personality. Our strengths lie in commercial, residential and multi-residential projects where we work on the small and the large. We’re flexible, agile and updateable but we do keep one thing consistent across the board; every project is led by YOUR vision and crafted by our tools and expertise."}))

(def for-architects-atom
  (atom {:bold "There are architects and then there’s Solari Architects."
         :paragraph " We’re the “un-architecty” architects (the ones that say architecty).  We’ve found (well, created) this little sweet spot in the industry. Basically we have a huge passion for architecture but not in a consuming ‘it’s my whole life’ way. It’s balanced and continually inspired and fuelled by all the non-architect things we welcome into our lives. We enjoy exploring in our weekends, spending time with our families and hanging out with friends and are always up to try new experiences and give things a go. We’ll make an effort to be aware of trends but not make them gospel. For example we completely missed the memo that thick rimmed glasses and showing a questionable amount of sockless ankle was the latest uniform…but we’re fine with that."}))

(def from-us-atom
  (atom {:bold "A gathering of ideas, images, thoughts, brainstorms, news and the miscellaneous interesting-ness."
         :paragraph ""}))

(def your-career-atom
  (atom {:main {:bold "Here’s some good advice:" :paragraph " Never choose to work for someone based on their brand or portfolio. Work for people who are going to teach you and make you better."}
         :paragraph-one "At Solari, our focus is on growing independent thinkers and architects with the confidence to see an entire project through from start to finish. We encourage boundary pushing and fresh thinking. Growing your career is absolutely up to you  - there are no glass ceilings here. Create your own luck through passionate work and being yourself and you’ll fit right in. "
         :paragraph-two "Whether you stay with us for ten years or two. We want you to leave as a better architect than when you came in. We’re satisfied if our contribution to the industry is talent, even if that talent outgrows us. Our focus is for you to learn and then teach. See the workings of the business instead of just being siloed into perfecting one aspect of the industry. "}))

(def projects-atom
  (atom {:projects [{:text "Homes are personal projects and we love that. When we take on a residential project we take on the thoughts, feelings, personality and unique circumstances of the client. We work closely with you to ensure that your home is exactly that – yours. You’re with us every step of the way, this not only makes absolute sense but undoubtedly delivers the best results. We share the challenges and successes with you and make you the expert of your own project by going at a pace that promotes attention to detail and clarity of thought from start to finish."
                     :category "Residential"
                     :projects [pr/project-01 pr/project-02 pr/project-03 pr/project-04]}

                    {:text "Solari’s success in the multi-unit residential development sector across New Zealand and Australia comes down to what we like to call ‘The Solari Way’. In a nutshell it’s an approach that balances the values and objectives of the developer, designer and tenants. Everyone involved with the project stands to benefit from such an insightful approach. Blending our understanding of commercial realities, how a target market perceives quality living spaces and how to effectively manage the design process from sketches to site, ensures our developments maintain their purpose and quality."
                     :category "Multi-unit Residential"
                     :projects [pr/project-05 pr/project-06 pr/project-07 pr/project-08 pr/project-09]}

                    {:text "At Solari we don’t define commercial buildings by their sheer scale but by their purpose.  We treat them as strategic assets, marketing tools and enablers of achieving business goals. We take full advantage of the power commercial and workplace design has to impact three key objectives shared by all businesses (including our own): efficiency, effectiveness and expression. "
                     :category "Commerical"
                     :projects [pr/project-10 pr/project-11]}]}))
(set-validator! projects-atom #((complement empty?) %))


(def process-atom
  (atom  {:text {:bold "It goes without saying we want the best possible outcome for you and your project." :paragraph " That’s exactly why we do what we do…  "}
          :short {:step1 "We listen to your goals & objectives."
                  :step2 "We translate your ideas, inspiration and words into design. This goes back and forth until we’re all speaking the same language. "
                  :step3 "We communicate the solution to the right team of collaborators to actualise your vision. "}
         :long [
                {:heading "It starts with a chat."
                 :paragraphs ["We get together and you tell us what you’re hoping to achieve. "
                              "The things we’re interested in are: the project site, your timeframes, your expected budget and your vision. We’re always happy to show you our previous work and talk to you about how we can work with you. You can bring cuttings, mood boards, words, scribbles, Pinterest boards, music - just anything that helps us get an idea of what you’ve got in mind.  Also, tell us who you are.  Knowing a little more about your lifestyle, family, hobbies, goals and an entertaining story all helps us put together a quote for our services."
                              "From there you can decide if you want to partner with us to work on your project (fingers crossed)."
                              ]}

                {:heading "Just so you know: Useful information."
                 :paragraphs ["We are members of the NZIA. It’s useful because we will provide you with a copy of our comprehensive ‘Agreement for Architect's Services’. This document outlines every step of the process from first sketch designs through to completion. You know, all that useful information. "
                              ]}

                {:heading "Refining your vision."
                 :paragraphs ["You decide you want to work with us. Good choice. "
                              "Now we work on refining your vision. We flesh out all the inspiration and ideas from our initial meeting to get an in-depth understanding of who you are and what we will be working towards together. "
                              "We form a clear brief that will act as a starting point for the concept development."
                              ]}

                {:heading "Getting to Know The Site."
                 :paragraphs ["Depending on the requirements of your project we begin gathering information. This includes things like the Certificate of Title, drainage plans, zoning and town planning information. We will take pictures of the site and can organise to have the site surveyed on your behalf for its contours and boundaries."
                              ]}

                {:heading "Concept Development."
                 :paragraphs ["This is the first time we’ll put our initial ideas in front of you for your feedback."
                              "From here we will work with you to initially develop the basic spatial relationships of the building and importantly the site. We will work with you to develop floor plans and some sketch imagery of the look and style of what is proposed. "
                              ]}

                {:heading "Turning A Concept Into Drawings."
                 :paragraphs ["It’s still early in the piece and the point of concept drawings is to encourage discussion. They can help you articulate what you do and don't like – and might even result in a complete change of direction. "
                              "This is all part of the process – it's important to remember that at this stage it's not about being in total agreement on every detail, but to get a strong sense of whether we are heading in the right direction and have really listened to what you've talked about up to this point."
                              "During this stage you also need to have a clear sense of budget and any issues that might impact upon that. "
                              ]}

                {:heading "Developing The Design & Budget Revisit. "
                 :paragraphs ["This is the stage where everything begins to take shape. We’ve agreed on the concept and now it’s time to show you the details. We do this with 3-D CAD drawings, floor plans, flows, elevations, cut-through section drawings and detail drawings. "
                              "Depending on the project requirements, size and budget we can also build a scale model to really show you how the project will finish up. "
                              "It’s a very exciting part of the process, but to stay on track we will revisit your budget again and talk over your priorities in terms of cost, time and quality. It is likely that a Quantity Surveyor will be asked to make an independent cost estimate."
                              ]}

                {:heading "Deciphering Architectural Plans."
                 :paragraphs ["We know it can be hard to visualise the finished product from architectural drawings. So please ask lots of questions and by all means do not gloss over anything you don’t understand. It’s really important to us that you see exactly what we see. "
                              ]}

                {:heading "The Nuts & Bolt Phase: Working Drawings and Building Consent. "
                 :paragraphs ["In this stage your project is documented, co-ordinated and made “build-ready”. We finalise a comprehensive package of drawings and literature that builders will price from and councils will process for Building Consent. The complexity of the project will determine how detailed this package needs to be in order to achieve building consent. "
                              "We apply for a building consent on your behalf. Each local authority has its own way of working fee structures and processing times. To ensure a stress free consent process we work in the consultation with councils as much as possible. "
                              "Naturally we will keep you in the loop every step of the way so you know how things are tracking."
                              ]}

                {:heading "What Building Contractor? Tendering and Procurement"
                 :paragraphs ["There are a number of ways to procure a building contractor. We can work with you to ascertain the best approach for your specific needs. There are a lot of issues to consider such as timeframe, budget, and desired quality that will have a bearing on your final decision. We will discuss all of the responses with you and we will work together to choose the best contractor for you and the project."
                              ]}

                {:heading "Contract Administration & Site Observation"
                 :paragraphs ["This is the construction phase of the job. We work with you and the construction team to ensure that what we have designed with you is realised. "
                              "The degree to which we administer and observe the building process will depend on the scope and complexity of your project. While a small simple project might only require occasional site visits, more complex projects where risk of non-compliance is higher, will require more frequent visits to review the work and approve payments to the builder."
                              "Throughout this process more often than not changes can occur. This is expected and we are experienced at managing and dealing with formal variations to the contracted works."
                              ]}

                {:heading "Almost finished."
                 :paragraphs ["As the build is nearing completion we undertake comprehensive inspections of the completed project and advise the builder of any defects that need fixing before signing off on practical completion and applying for code compliance certification from the local council."
                              "Upon a satisfactory level of completion we are able to certify practical completion ready for you to move in. (Hooray!) "
                              ]}

                {:heading "Defects Period."
                 :paragraphs ["Following practical completion there is a period of time in which the contractors are responsible for finishing any minor defects. At the end of this period we will inspect your project and make sure that any blemishes are addressed by the builder prior to issuing a defects liability certificate. "
                              "Final monetary retentions are not released until this certificate is issued."
                              ]}

                {:heading "Overall."
                 :paragraphs ["It is our preference is to be involved with you right through the construction process. We have years of experience in the successful delivery of numerous types of projects – it just makes sense that we come along for “the ride”. "
                              "Construction is a complex process and is generally / naturally unfamiliar territory for most people. We therefore strongly believe we bring great value to this part of your project.  There are different ways we can structure our involvement and we have specific NZIA construction contracts depending on our exact role."
                              ]}
                ]}))

(def faqs-atom (atom {:text "You’re not supposed to know it all before coming to us. Whether it’s your first time working with an architecture firm or you’ve lost count, there’s never a bad time to ask questions like:"
                 :questions [
                             {:q "How does Solari charge?"
                              :a ""}

                             {:q "How involved should I be in the architectural design process?"
                              :a ""}

                             {:q "What technical programs do you use?"
                              :a ""}

                             {:q "What types of insurance do you carry?"
                              :a ""}

                             {:q "How can you be sure a project doesn't go over budget?"
                              :a ""}

                             {:q "What's Solari's approach t sustainability?"
                              :a ""}

                             {:q "How should I cmmunicate what I want to Solari?"
                              :a "Words, pictures, examples, feelings, thoughts – in whatever way you choose! "}

                             {:q "Who does Solari Collaborate with to complete a project?"
                              :a ""}

                             {:q "What sized project is Solari capable of taking on?"
                              :a "Due to James time in Australia he has working relationships with companies such as Woods Bagot, Cox, Scott Carber, GHD Woodhead. Across the board these firms have international experience in sectors such as Aviation, Hospitality, Workplace and Retail. We can partner with these firms to offer the best international expertise whilst still dealing with a local firm."}

                             {:q "Why use an architect?"
                              :a "Architects are design focussed. Now that doesn’t mean we just make our projects look good – although we do that too. We make them feel good and function well. We design structures that respond to their wider context and look past the “now” and look at how a"}

                             {:q "It seems so expensive to use an Architect?"
                              :a "Firstly (Practically thinking) Architects are subject to a statutory code of practice and have Professional Indemnity Insurance to protect their clients.Secondly. In your lifetime, a house is one of the biggest investments you will make. So why not make the most of that investment and ensure its long-term value. Take advantage of the opportunity to do it right. As your architect we will ensure that quality products are used, the space is right for your needs now and in the future, the design is created taking advantage of the best that your site has to offer. This is no cookie cutter experience"}

                             ]}))

(def the-team-atom
  (atom {:text {:bold "The Solari team is your team."  :paragraph " As collaborators there is no \"our way\" verse \"your way\". Instead we blend your goals with our knowledge of the industry. We aren’t big on baffling you with fancy architecture jargon because it compromises our love of clarity and progress. We’re a transparent, honest team and are pretty happy being described as \"normal people\" by those who work with us. We all share a love of architecture, life balance, creativity and fun – which makes for an enjoyable common ground."}
         :title "Your Team"
         :team-members [{:name "James Solari"
                         :memberid "jsolari"
                         :profilepics {:hipster "/img/teampics/jsolari_hipster.jpg"
                                       :everyday "/img/teampics/jsolari_everyday.jpg"}
                         :Role "Director & Lead Architect"
                         :how ["After 14 years working for other people I decided it was time to step out on my own and build a business that focussed on my specific areas of expertise. I was fortunate to find a group of talented (and fun) people to work with that share my vision and deliver great results for our clients."
                               "I have unique experience in multi-unit residential builds as a result of my time at Woodhead in Australia.  I believe that New Zealand’s market it still relatively immature. As our population grows we need to intensify our housing solutions. But we need to do it in a way that fosters communities, creates safe and healthy living environments and enables people to thrive."
                               "Having said that, my passion and expertise is not limited to multi-unit solutions. I love to work on stand-alone houses, commercial and hospitality solutions. I’m excited about exploring and balancing the cross-overs between how we live, work and play."]
                         :goals "They’re big ones. And I want to create a strong and enduring company with the right people to help me achieve them. Solari Architects is a family business. My goal is to create a company that develops talent and enables people to be the best that they can be; both as members of their families and communities and as architects and key people within the business."
                         :advice ""
                         :outside ""
                         :polaroid "(Something shorter please!)"}

                        {:name "Catherine Solari"
                         :memberid "csolari"
                         :profilepics {:hipster "/img/teampics/csolari_hipster.jpg"
                                       :everyday "/img/teampics/csolari_everyday.jpg"}
                         :Role "Corporate Services Manager"
                         :how ["After 17 years working in sales roles in the IT industry James and I decided to start a family and along came our little guy Hugo. Instead of returning to the land of IT after a year of looking after that wee guy I decided it was a much better idea to jump in and help out James with his flourishing little business. It was doing so well he didn’t have time to run it, so with my experience in large corporates and with sound business practices I came on board to lend a hand. Over the last 2 years we have substantially grown the business from just a 3 person operation to the team of 7 it is now and with all the work we have going there James is flat out being an Architect.  Its great fun and I’m really proud of how things are going."
                               ]
                         :goals ""
                         :advice "Do what you really love to do and make sure you are doing it with people you actually like. Life is pretty horrible if you aren’t."
                         :outside ""
                         :polaroid "Do what you really love to do and make sure you are doing it with people you actually like. Life is pretty horrible if you aren’t."}

                        {:name "Matt Cane"
                         :memberid "mcane"
                         :profilepics {:hipster "/img/teampics/mcane_hipster.jpg"
                                       :everyday "/img/teampics/mcane_everyday.jpg"}
                         :Role "Senior Technician"
                         :how ["I recently returned home from the UK where I predominantly worked on community housing projects. During this time I worked with Not-for-Profit Housing organisations such as Forth Housing Association, Places for People and Dunedin Canmore Housing Association.  I have worked with the UK standards such as Housing for Varying Needs, Secured by Design, BRE Eco Homes."]
                         :goals ""
                         :advice "Don’t cry over spilt milk. Literally relevant these days with a young child."
                         :outside "You’ll find me hitting the beach whether for play or just to relax with the family. It’s a great way to spend my free time. "
                         :polaroid "Don’t cry over spilt milk. Literally relevant these days with a young child."}

                        {:name "Melanie Zyteka"
                         :memberid "mzyteka"
                         :profilepics {:hipster "/img/teampics/mzyteka_hipster.jpg"
                                       :everyday "/img/teampics/mzyteka_everyday.jpg"}
                         :Role "Senior Technician"
                         :how ["After graduating as an Architectural Technician from Wellington Polytechnic in 1998, I moved to London where I worked on a variety of projects. The call of home grew louder and I returned in 2005 to work at Archaus Architects until joining Solari Architects at the end of 2013. My career so far has included commercial, multi-storey residential and hospitality projects. "
                               "I was thrilled to join the Solari team as they’re such a vibrant company who thrive on producing great designs. They also have a really supportive team structure. "
                               ]
                         :goals ""
                         :advice "We are a long time dead so make sure you enjoy your life and that includes where you work!"
                         :outside ""
                         :polaroid "We are a long time dead so make sure you enjoy your life and that includes where you work!"}

                        {:name "Monique Addis"
                         :memberid "maddis"
                         :profilepics {:hipster "/img/teampics/maddis_hipster.jpg"
                                       :everyday "/img/teampics/maddis_everyday.jpg"}
                         :Role "Senior Designer"
                         :how ["Monique brings to the Solari Architects team 17 years of well-rounded experience in all facets of the design and project delivery process.   Monique has been involved in award winning high quality residential and commercial projects of mixed scale, throughout New Zealand in urban, rural and coastal settings. From 2008, Monique took time out of the profession to raise her young family and undertake her own architectural project."
                               "During this time she continued with contract architectural work before joining the team at Solari Architects mid-2013. Monique is excellent at multi-tasking and ensures all aspects of her projects are completed to the highest standard.  She enjoys the collaborative process of taking a project from the design brief through to construction and completion."
                               ]
                         :goals ""
                         :advice "Don’t leave things until the last minute. Do it once, do it right!"
                         :outside ""
                         :polaroid "Don’t leave things until the last minute. Do it once, do it right!"}

                        {:name "Alex Brimmicombe"
                         :memberid "abrimmicombe"
                         :profilepics {:hipster "/img/teampics/abrimmicombe_hipster.jpg"
                                       :everyday "/img/teampics/abrimmicombe_everyday.jpg"}
                         :Role "Architectural Graduate"
                         :how ["In mid 2015 the stars seemed to align. I was lucky enough to be taken on board by the team at Solari Architects. Before this, I spent ten months in a smaller Wellington firm after completing my Master’s degree at Victoria University. My final year of study focussed on safety through spatial design and creating future proof public architecture."
                               "Upon meeting James I could see that I had come to the perfect place to advance my skills and develop my knowledge as a graduate architect. A personal interest in contemporary residential and commercial architecture is being further developed by being a part of the Solari team."
                               ]
                         :goals ""
                         :advice ""
                         :outside "You’ll find me getting dangerously airborne on a bmx or mountain bike, taking blurry photos, or working under an impractically low car."
                         :polaroid "I love the supportive environment, vast range of knowledge, and the exquisite coffee at Solari Architects! "}

                        {:name "Molly Marshall"
                         :memberid "mmarshall"
                         :profilepics {:hipster "/img/teampics/mmarshall_hipster.jpg"
                                       :everyday "/img/teampics/mmarshall_everyday.jpg"}
                         :Role "Architectural Graduate"
                         :how ["I Joined the Solari team full time in 2015. Before that I was working part time with them whilst completeing my Master’s thesis at Victoria university. I’m enjoying the opportunity to be involved in all areas of the architectural process. "
                               ]
                         :goals ""
                         :advice ""
                         :outside "you’ll find me playing social netball and café hopping around Wellington."
                         :polaroid "You’ll find me playing social netball and café hopping around Wellington."}
                        ]
                    }))

(defresource career
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context]
                          @your-career-atom)
             :put! (fn [ctx]
                     (reset! your-career-atom (:projects (:params (:request ctx)))))
             :available-media-types ["application/edn"])

(defresource team
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context]
                          @the-team-atom)
             :put! (fn [ctx]
                     (reset! the-team-atom (:projects (:params (:request ctx)))))
             :available-media-types ["application/edn"])


(defresource faqs
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context]
                          @faqs-atom)
             :put! (fn [ctx]
                     (reset! faqs-atom (:projects (:params (:request ctx)))))
             :available-media-types ["application/edn"])


(defresource process
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context]
                          @process-atom)
             :put! (fn [ctx]
                     (reset! process-atom (:projects (:params (:request ctx)))))
             :available-media-types ["application/edn"])


(defresource projects
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context]
                          (let
                            [pred (get-in context [:request :params :query])]
                            (cond
                              (= pred "full-info") @projects-atom
                              (= pred "projects-only") [pr/project-01 pr/project-02 pr/project-03 pr/project-04
                                                        pr/project-05 pr/project-06 pr/project-07 pr/project-08
                                                        pr/project-09 pr/project-10 pr/project-11]
                              :else (str "Method does not exist: " pred))))
             :put! (fn [ctx]
                     (reset! projects-atom (:projects (:params (:request ctx)))))
             :available-media-types ["application/edn"])


(defresource home
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context]
                          @home-page-atom)
             :put! (fn [ctx]
                     (reset! home-page-atom (:projects (:params (:request ctx)))))
             :available-media-types ["application/edn"])

(defresource residential
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context]
                          @residential-atom)
             :put! (fn [ctx]
                     (reset! residential-atom (:projects (:params (:request ctx)))))
             :available-media-types ["application/edn"])

(defresource multi-unit
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context]
                          @multi-unit-atom)
             :put! (fn [ctx]
                     (reset! multi-unit-atom (:projects (:params (:request ctx)))))
             :available-media-types ["application/edn"])

(defresource commercial
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context]
                          @commercial-atom)
             :put! (fn [ctx]
                     (reset! commercial-atom (:projects (:params (:request ctx)))))
             :available-media-types ["application/edn"])

(defresource you
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context]
                          @for-you-atom)
             :put! (fn [ctx]
                     (reset! for-you-atom (:projects (:params (:request ctx)))))
             :available-media-types ["application/edn"])

(defresource architects
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context]
                          @for-architects-atom)
             :put! (fn [ctx]
                     (reset! for-architects-atom (:projects (:params (:request ctx)))))
             :available-media-types ["application/edn"])

(defresource us
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context]
                          @from-us-atom)
             :put! (fn [ctx]
                     (reset! from-us-atom (:projects (:params (:request ctx)))))
             :available-media-types ["application/edn"])


