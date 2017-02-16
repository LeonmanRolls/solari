(ns solari.model
  (:require [liberator.core :refer [defresource resource request-method-in]]
            [environ.core :refer [env]]
            [clojure.core.async :refer [>! <! >!! <!! go chan buffer close! thread alts! alts!!]]
            [clojure.string :as string]
            [solari.projects :as pr]
            [clojure.walk :as w]
            [clojure.java.jdbc :as sql]
            [clojure.java.jdbc.deprecated :as sql-old])
  (:use
    [twitter.oauth]
    [twitter.callbacks]
    [twitter.callbacks.handlers]
    [twitter.api.restful])
  (:import
    (twitter.callbacks.protocols SyncSingleCallback)))

(def db (System/getenv "HEROKU_POSTGRESQL_ROSE_URL"))

(def my-creds (make-oauth-creds (System/getenv "TWITTER-ONE")
                                (System/getenv "TWITTER-TWO")
                                (System/getenv "TWITTER-THREE")
                                (System/getenv "TWITTER-FOUR")))

#_(defn single-game-info-query [context db]
  (sql/query
    db
    [(str
       "SELECT "
       (get-in
         context
         [:request :params :fields])
       " from games where gbcid="
       (get-in
         context
         [:request :params :gameid]))]))

(def home-page-data
  {:bold ["Come on in. We're Solari architects."]
         :paragraph [" Our studio is based in Wellington and our thoughts, projects and experiences span New Zealand, Australia and beyond. When working with you we focus on speaking a common language  - you’ll find no architectural lingo here. We take your vision from pictures, words, half-formed or full-formed ideas and “ya knows” and translate them into architecture representative of your values, goals and personality. Our strengths lie in commercial, residential and multi-residential projects where we work on the small and the large. We’re flexible, agile and updateable but we do keep one thing consistent across the board; every project is led by YOUR vision and crafted by our tools and expertise."]})

(def residential-data
  {:bold ["Homes are personal projects - we like that."]
         :paragraph [" When designing a residential project we take on the thoughts, feelings, personality and unique circumstances of the client (that’s you). We work closely with you to ensure that your home is exactly that – yours. You’re with us every step of the way. This not only makes absolute sense but it undoubtedly delivers the best results. We share the challenges and successes with you. And make you the expert of your own project by going at a pace that promotes attention to detail and clarity of thought from start to finish."] })

(def multi-unit-data
  {:bold ["Solari’s success in the multi-unit residential development sector across New Zealand and Australia comes down to what we like to call ‘The Solari Way’."]
         :paragraph [" In a nutshell it’s an approach that balances the values and objectives of the developer, designer and tenants. Everyone involved with the project stands to benefit from such an insightful approach. Blending our understanding of commercial realities, how a target market perceives quality living spaces and how to effectively manage the design process from sketches to site, ensures our developments maintain their purpose and quality."] })

(def commercial-data
  {:bold ["At Solari we don’t define commercial buildings by their sheer scale but by their purpose. "]
         :paragraph [" We treat them as strategic assets, marketing tools and enablers of achieving business goals. We take full advantage of the power commercial and workplace design has to impact three key objectives shared by all businesses (including our own): efficiency, effectiveness and expression. "] })

(def for-you-data
  {:bold ["When working with you we focus on speaking a common language."]
         :paragraph [" We take your vision from pictures, words, half-formed or full-formed ideas and “ya knows” and translate them into architecture representative of your values, goals and personality. Our strengths lie in commercial, residential and multi-residential projects where we work on the small and the large. We’re flexible, agile and updateable but we do keep one thing consistent across the board; every project is led by YOUR vision and crafted by our tools and expertise."] })

(def for-architects-data
  {:bold ["There are architects and then there’s Solari Architects."]
         :paragraph [" We’re the “un-architecty” architects (the ones that say architecty).  We’ve found (well, created) this little sweet spot in the industry. Basically we have a huge passion for architecture but not in a consuming ‘it’s my whole life’ way. It’s balanced and continually inspired and fuelled by all the non-architect things we welcome into our lives. We enjoy exploring in our weekends, spending time with our families and hanging out with friends and are always up to try new experiences and give things a go. We’ll make an effort to be aware of trends but not make them gospel. For example we completely missed the memo that thick rimmed glasses and showing a questionable amount of sockless ankle was the latest uniform…but we’re fine with that."] }
  )

(def from-us-data
  {:text {:bold ["A gathering of ideas, images, thoughts, brainstorms, news and the miscellaneous interesting-ness."]
         :paragraph [""] }
   :instagram-data []})

(def your-career-data
  {:main {:bold ["Here’s some good advice:  Never choose to work for someone based on their brand or portfolio. Work for people who are going to teach you and make you better."]   }
         :paragraph-one ["At Solari, our focus is on growing independent thinkers and architects with the confidence to see an entire project through from start to finish. We encourage boundary pushing and fresh thinking. Growing your career is absolutely up to you  - there are no glass ceilings here. Create your own luck through passionate work and being yourself and you’ll fit right in. "]
         :paragraph-two ["Whether you stay with us for ten years or two. We want you to leave as a better architect than when you came in. We’re satisfied if our contribution to the industry is talent, even if that talent outgrows us. Our focus is for you to learn and then teach. See the workings of the business instead of just being siloed into perfecting one aspect of the industry. "] }
  )

(def projects-data
  {:projects [{:text ["Homes are personal projects and we love that. When we take on a residential project we take on the thoughts, feelings, personality and unique circumstances of the client. We work closely with you to ensure that your home is exactly that – yours. You’re with us every step of the way, this not only makes absolute sense but undoubtedly delivers the best results. We share the challenges and successes with you and make you the expert of your own project by going at a pace that promotes attention to detail and clarity of thought from start to finish."]
                     :category ["Residential"]
                     :projects [pr/project-01 pr/project-02 pr/project-03 pr/project-04]}

                    {:text ["Solari’s success in the multi-unit residential development sector across New Zealand and Australia comes down to what we like to call ‘The Solari Way’. In a nutshell it’s an approach that balances the values and objectives of the developer, designer and tenants. Everyone involved with the project stands to benefit from such an insightful approach. Blending our understanding of commercial realities, how a target market perceives quality living spaces and how to effectively manage the design process from sketches to site, ensures our developments maintain their purpose and quality."]
                     :category ["Multi-unit Residential"]
                     :projects [pr/project-05 pr/project-06 pr/project-07 pr/project-08 pr/project-09]}

                    {:text ["At Solari we don’t define commercial buildings by their sheer scale but by their purpose.  We treat them as strategic assets, marketing tools and enablers of achieving business goals. We take full advantage of the power commercial and workplace design has to impact three key objectives shared by all businesses (including our own): efficiency, effectiveness and expression. "]
                     :category ["Commerical"]
                     :projects [pr/project-10 pr/project-11]}]})


(def process-data
  {:text {:bold ["It goes without saying we want the best possible outcome for you and your project."]  :paragraph [" That’s exactly why we do what we do…  "] }

   :left-nav [{:label "Short version" :selected false} {:label "Long version" :selected false}]

   :short {:step1 ["1. We listen to your goals & objectives."]
           :step2 ["2. We translate your ideas, inspiration and words into design. This goes back and forth until we’re all speaking the same language. "]
           :step3 ["3. We communicate the solution to the right team of collaborators to actualise your vision. "] }

   :long [{:title ["It starts with a chat."]
           :content [["We get together and you tell us what you’re hoping to achieve. "]
                     ["The things we’re interested in are: the project site, your timeframes, your expected budget and your vision. We’re always happy to show you our previous work and talk to you about how we can work with you. You can bring cuttings, mood boards, words, scribbles, Pinterest boards, music - just anything that helps us get an idea of what you’ve got in mind.  Also, tell us who you are.  Knowing a little more about your lifestyle, family, hobbies, goals and an entertaining story helps us put together a together a complete picture of requirements and allows us to give you an accurate quote for our services."]
                       ["From there you can decide if you want to partner with us to work on your project (fingers crossed)."]]}

          {:title ["Just so you know: Useful information."]
           :content [["We are members of the NZIA. It’s useful because we will provide you with a copy of our comprehensive ‘Agreement for Architect's Services’. This document outlines every step of the process from first sketch designs through to completion. You know, all that useful information. "]
                        ]}

          {:title ["Refining your vision."]
           :content [["You decide you want to work with us. Good choice. "]
                      ["Now we work on refining your vision. We flesh out all the inspiration and ideas from our initial meeting to get an in-depth understanding of who you are and what we will be working towards together. "]
                       ["We form a clear brief that will act as a starting point for the concept development."]
                        ]}

          {:title ["Getting to Know The Site."]
           :content [["Depending on the requirements of your project we begin gathering information. This includes things like the Certificate of Title, drainage plans, zoning and town planning information. We will take pictures of the site and can organise to have the site surveyed on your behalf for its contours and boundaries."]
                        ]}

          {:title ["Concept Development."]
           :content [["This is the first time we’ll put our initial ideas in front of you for your feedback."]
                     ["From here we will work with you to initially develop the basic spatial relationships of the building and importantly the site. We will work with you to develop floor plans and some sketch imagery of the look and style of what is proposed. "]
                        ]}

          {:title ["Turning A Concept Into Drawings."]
           :content [["It’s still early in the piece and the point of concept drawings is to encourage discussion. They can help you articulate what you do and don't like – and might even result in a complete change of direction. "]
                     ["This is all part of the process – it's important to remember that at this stage it's not about being in total agreement on every detail, but to get a strong sense of whether we are heading in the right direction and have really listened to what you've talked about up to this point."]
                     ["During this stage you also need to have a clear sense of budget and any issues that might impact upon that. "]
                     ]}

          {:title ["Developing The Design & Budget Revisit. "]
           :content [["This is the stage where everything begins to take shape. We’ve agreed on the concept and now it’s time to show you the details. We do this with 3-D CAD drawings, floor plans, flows, elevations, cut-through section drawings and detail drawings. "]
                     ["Depending on the project requirements, size and budget we can also build a scale model to really show you how the project will finish up. "]
                     ["It’s a very exciting part of the process, but to stay on track we will revisit your budget again and talk over your priorities in terms of cost, time and quality. It is likely that a Quantity Surveyor will be asked to make an independent cost estimate."]
                        ]}

          {:title ["Deciphering Architectural Plans."]
           :content [["We know it can be hard to visualise the finished product from architectural drawings. So please ask lots of questions and by all means do not gloss over anything you don’t understand. It’s really important to us that you see exactly what we see. "]
                        ]}

          {:title ["The Nuts & Bolt Phase: Working Drawings and Building Consent. "]
           :content [["In this stage your project is documented, co-ordinated and made “build-ready”. We finalise a comprehensive package of drawings and literature that builders will price from and councils will process for Building Consent. The complexity of the project will determine how detailed this package needs to be in order to achieve building consent. "]
                     ["We apply for a building consent on your behalf. Each local authority has its own fee structures, processing times and way of working. To ensure a stress free consent process we work in consultation with councils as much as possible. "]
["Naturally we will keep you in the loop every step of the way so you know how things are tracking."]
                        ]}

          {:title ["What Building Contractor? Tendering and Procurement"]
           :content [["There are a number of ways to procure a building contractor. We can work with you to ascertain the best approach for your specific needs. There are a lot of issues to consider such as timeframe, budget, and desired quality that will have a bearing on your final decision. We will discuss all of the responses with you and we will work together to choose the best contractor for you and the project."]
                        ]}

          {:title ["Contract Administration & Site Observation"]
           :content [
                    ["This is the construction phase of the job. We work with you and the construction team to ensure that what we have designed with you is realised. "]
                     ["The degree to which we administer and observe the building process will depend on the scope and complexity of your project. While a small simple project might only require occasional site visits, more complex projects where risk of non-compliance is higher, will require more frequent visits to review the work and approve payments to the builder."]
["Throughout this process more often than not changes can occur. This is expected and we are experienced at managing and dealing with formal variations to the contracted works."]
                        ]}

          {:title ["Almost finished."]
           :content [
                    ["As the build is nearing completion we undertake comprehensive inspections of the completed project and advise the builder of any defects that need fixing before signing off on practical completion and applying for code compliance certification from the local council."]
                    ["Upon a satisfactory level of completion we are able to certify practical completion ready for you to move in. (Hooray!) "]
                        ]}

          {:title ["Defects Period."]
           :content [
                    ["Following practical completion there is a period of time in which the contractors are responsible for finishing any minor defects. At the end of this period we will inspect your project and make sure that any blemishes are addressed by the builder prior to issuing a defects liability certificate. "]
                     ["Final monetary retentions are not released until this certificate is issued."]
                        ]}

          {:title ["Overall."]
           :content [
                    ["It is our preference is to be involved with you right through the construction process. We have years of experience in the successful delivery of numerous types of projects – being on your team means we bring all that to the table and more."]
["Construction is a complex process and is generally / naturally unfamiliar territory for most people. We therefore strongly believe we bring great value to this part of your project.  There are different ways we can structure our involvement and we have specific NZIA construction contracts depending on our exact role."]
                        ]}
          ]})

(def faqs-data {:text {:bold ["You’re not supposed to know it all before coming to us."]
                       :paragraph [" Whether it’s your first time working with an architecture firm or you’ve lost count, there’s never a bad time to ask questions like:"] }
                 :questions [

                             {:title ["How involved should I be in the architectural design process?"]
                              :content [["You can have as much or as little to do with your project, as you like. We can steer the ship or simply fill the gaps in your plan. Remember, your project is exactly that – yours. It is your vision guided by our experience and expertise. "]]}

                             {:title ["How should I communicate what I want to Solari?"]
                              :content [["Words, pictures, examples, feelings, thoughts – in whatever way you choose! Share your pinterest board or note pad of ideas…it could even be an interpretive dance. We’re open to whatever it takes to get us all on the same page."]]}

                             {:title ["What technical programs do you use?"]
                              :content [["We use 3D-CAD to bring your project to life before putting a nail in anything. We understand that even with all the mood-boards and scrap books in the world, it can be hard to visualise the final product. Designing a 3D render always erases the guesswork or confusion for our clients. "]]}

                             {:title ["What types of insurance do you carry?"]
                              :content [["As registered Architects we are subject to a statutory code of practice and have ‘Professional Indemnity Insurance’ to protect our clients. "]]}

                             {:title ["How can you be sure a project doesn't go over budget?"]
                              :content [["Firstly – you can relax in knowing this isn’t the first time we’ve done something like this. Our vast experience has taught us what to look out for and to always be 20 steps ahead of everyone else. To add to this, we’re transparent, honest and have an obsession with planning - but are equally quick on our feet and able to calculate a new direction if need be. From start to finish we will always revisit your budget and provide accurate quotes, so there are no unexpected surprises. "]]}

                             {:title ["What sized project is Solari capable of taking on?"]
                              :content [["MYTH: The bigger the team, the more capable they are of taking on a bigger project. "]
                                        ["Every industry is changing in this respect. A task that once took a team of 10 to complete can now be done by 1  – thanks technology. Our focus is on being the smartest firm in town – not the biggest.  You see, at Solari, we don’t like waste. Wasted time, wasted material and most of all wasted money. "]
                                        ["Everything is streamlined from our process to the development stage and we believe having a small, tight-knit team allows us to do this. We’ve minimised the amount of stepping-stones it takes to develop a project – because nobody likes a game of Chinese whispers when there’s a lot at stake. "]
                                        ["Over his time spent in Australia, our founder James has developed fantastic working relationships with companies such as Woods Bagot, Cox, Scott Carver and GHD Woodhead. "]
                                        ["Across the board these firms have international experience in sectors such as Aviation, Hospitality, Workplace and Retail. Because we love collaboration we can always partner with these firms to offer the best international expertise whilst still dealing with a local firm."]
                                        ]}

                             {:title ["Why use an architect?"]
                              :content [["Architects are design focussed. Now that doesn’t mean we just make our projects look good – although we do that too. We make them feel good and function well. We design structures that respond to their wider context. We look past the “now” to deliver a product that will stand the test of time. Ensuring it will respond to future changes in the economic environment, lifestyle and demands of future buyers, investors or your growing family."]]}

                             {:title ["It seems so expensive to use an Architect?"]
                              :content [["We hear this often, to which we say; every dollar goes somewhere. And we can assure you we’re not having a good old laugh on our private yacht, popping bottles of Champagne once your project is completed. We’re firm believers in truly giving our clients what they pay for. "]
                                        ["Your project is an investment. If it’s your own home it may be one of the biggest investments you make in your lifetime. So take the opportunity to do it right and ensure its long-term value. As your architects we will fuss over quality products, creating a space that is right for you now AND in the future and squeezing every drop of value out of your site with a design that is tailored to your “wish list”. We don’t cut corners and you’re not just another job that needs to be “processed”. We genuinely care for our client’s satisfaction and spend a lot of time putting ourselves in your shoes to design a project that is uniquely you. "]
                                        ["On a practical note: Registered Architects are subject to a statutory code of practice and have ‘Professional Indemnity Insurance’ to protect their clients. So you can sleep well at night knowing that we’ve got your back. "]
                                        ]}

                             ]})

(def the-team-data
  {:text {:bold ["The Solari team is your team."]
          :paragraph [" As collaborators there is no \"our way\" versus \"your way\". Instead we blend your goals with our knowledge of the industry. We aren’t big on baffling you with fancy architecture jargon because it compromises our love of clarity and progress. We’re a transparent, honest team and are pretty happy being described as \"normal people\" by those who work with us. We all share a love of architecture, life balance, creativity and fun – which makes for an enjoyable common ground."] }
   :title ["Your Team"]
   :leaderboard {:architect ["/img/leaderboards/group_photo_everyday.jpg"]  :hipster ["/img/leaderboards/group_photo_hipster.jpg"]}
         :team-members [{:name ["James Solari"]
                         :memberid ["jsolari"]
                         :profilepics {:hipster ["/img/teampics/jsolari_hipster.jpg"]
                                       :everyday ["/img/teampics/jsolari_everyday.jpg"]}
                         :Role ["Director & Lead Architect"]
                         :how [["After 14 years working for other people I decided it was time to step out on my own and build a business that focussed on my specific areas of expertise. I was fortunate to find a group of talented (and fun) people to work with that share my vision and deliver great results for our clients."]
                               ["I have unique experience in multi-unit residential builds as a result of my time at Woodhead in Australia.  I believe that New Zealand’s market it still relatively immature. As our population grows we need to intensify our housing solutions. But we need to do it in a way that fosters communities, creates safe and healthy living environments and enables people to thrive."]
                               ["Having said that, my passion and expertise is not limited to multi-unit solutions. I love to work on stand-alone houses, commercial and hospitality solutions. I’m excited about exploring and balancing the cross-overs between how we live, work and play."]]
                         :goals ["They’re big ones. And I want to create a strong and enduring company with the right people to help me achieve them. Solari Architects is a family business. My goal is to create a company that develops talent and enables people to be the best that they can be; both as members of their families and communities and as architects and key people within the business."]
                         :contact ["james@solariarchitects.com"]
                         :advice ["Own your projects, whether individually on a small project, or as part of a team on something larger, buy into it and know it inside out."]
                         :outside [""]
                         :texttitle ["WE HAVE A LAUGH."]
                         :textpara ["Cue James in a bald cap. Need we say any more?"]
                         }

                        {:name ["Catherine Solari"]
                         :memberid ["csolari"]
                         :profilepics {:hipster ["/img/teampics/csolari_hipster.jpg"]
                                       :everyday ["/img/teampics/csolari_everyday.jpg"] }
                         :Role ["Corporate Services Manager"]
                         :how [["After 17 years working in sales roles in the IT industry James and I decided to start a family and along came our little guy Hugo. Instead of returning to the land of IT after a year of looking after that wee guy I decided it was a much better idea to jump in and help out James with his flourishing little business. It was doing so well he didn’t have time to run it, so with my experience in large corporates and with sound business practices I came on board to lend a hand. Over the last 2 years we have substantially grown the business from just a 3 person operation to the team of 7 it is now and with all the work we have going there James is flat out being an Architect.  Its great fun and I’m really proud of how things are going."]
                               ]
                         :goals [""]
                         :contact ["catherine@solariarchitects.com"]
                         :advice ["Do what you really love to do and make sure you are doing it with people you actually like. Life is pretty horrible if you aren’t."]
                         :outside [""]
                         :polaroid ["Do what you really love to do and make sure you are doing it with people you actually like. Life is pretty horrible if you aren’t."]
                         :texttitle ["WE HAVE A LIFE (that isn’t work)."]
                         :textpara ["Whether you have a family of 4 small children, you coach the local badminton team or you’re into Comic-con you need to have YOUR own time to enjoy YOUR life. We respect and welcome that. Sure, there may be occasions where we all have to put in the extra mile but it’s not expected that you do it 52 weeks of the year just to get recognized OR be valued.  "]
                         }

                        {:name ["Matt Cane"]
                         :memberid ["mcane"]
                         :profilepics {:hipster ["/img/teampics/mcane_hipster.jpg"]
                                       :everyday ["/img/teampics/mcane_everyday.jpg"] }
                         :Role ["Senior Technician"]
                         :contact ["matt@solariarchitects.com"]
                         :how [["I recently returned home from the UK where I predominantly worked on community housing projects. During this time I worked with Not-for-Profit Housing organisations such as Forth Housing Association, Places for People and Dunedin Canmore Housing Association.  I have worked with the UK standards such as Housing for Varying Needs, Secured by Design, BRE Eco Homes."] ]
                         :goals [""]
                         :advice ["Don’t cry over spilt milk. Literally relevant these days with a young child."]
                         :outside ["You’ll find me hitting the beach whether for play or just to relax with the family. It’s a great way to spend my free time. "]
                         :polaroid ["Don’t cry over spilt milk. Literally relevant these days with a young child."]
                         :texttitle ["MAN, WE LOVE WHAT WE DO. "]
                         :textpara ["We are all a bunch of architecture nerds at heart. The challenge of a project with a complex site or budget constraints is exciting as it forces creativity and efficiency.  We crave opportunities to solve real housing problems through great living solutions more than chocolate, coffee and wine blended together. We continue to educate ourselves at all times to be the trusted advisor that our clients need us to be."]
                         }

                        {:name ["Melanie Zytecka"]
                         :memberid ["mzytecka"]
                         :profilepics {:hipster ["/img/teampics/mzyteka_hipster.jpg"]
                                       :everyday ["/img/teampics/mzyteka_everyday.jpg"] }
                         :Role ["Senior Technician"]
                         :how [["After graduating as an Architectural Technician from Wellington Polytechnic in 1998, I moved to London where I worked on a variety of projects. The call of home grew louder and I returned in 2005 to work at Archaus Architects until joining Solari Architects at the end of 2013. My career so far has included commercial, multi-storey residential and hospitality projects. "]
                               ["I was thrilled to join the Solari team as they’re such a vibrant company who thrive on producing great designs. They also have a really supportive team structure. "]
                               ]
                         :contact ["melanie@solariarchitects.com"]
                         :goals [""]
                         :advice ["We are a long time dead so make sure you enjoy your life and that includes where you work!"]
                         :outside [""]
                         :polaroid ["We are a long time dead so make sure you enjoy your life and that includes where you work!"]
                         :texttitle ["WE PUT OUR BEST FOOT FORWARD. ALWAYS."]
                         :textpara ["We know it’s cliché but the world is a village and our reputation is everything.  Our work is the most vocal representation of our brand. That’s everything from the way we communicate with our clients, consultants and local authorities, the documentation of our work, our interaction with contractors and of course the quality of the build. Every person we do business with and every piece of work that we do reflects the business. We make sure we do the best we can every single day. "]
                         }

                        {:name ["Monique Addis"]
                         :memberid ["maddis"]
                         :profilepics {:hipster ["/img/teampics/maddis_hipster.jpg"]
                                       :everyday ["/img/teampics/maddis_everyday.jpg"] }
                         :Role ["Senior Designer"]
                         :how [["Monique brings to the Solari Architects team 17 years of well-rounded experience in all facets of the design and project delivery process.   Monique has been involved in award winning high quality residential and commercial projects of mixed scale, throughout New Zealand in urban, rural and coastal settings. From 2008, Monique took time out of the profession to raise her young family and undertake her own architectural project."]
                               ["During this time she continued with contract architectural work before joining the team at Solari Architects mid-2013. Monique is excellent at multi-tasking and ensures all aspects of her projects are completed to the highest standard.  She enjoys the collaborative process of taking a project from the design brief through to construction and completion."]
                               ]
                         :goals [""]
                         :contact ["monique@solariarchitects.com"]
                         :advice ["Don’t leave things until the last minute. Do it once, do it right!"]
                         :outside [""]
                         :polaroid ["Don’t leave things until the last minute. Do it once, do it right!"]
                         :texttitle ["TEAM ON THREE."]
                         :textpara ["We work as a team. No one gets hung out to dry and there are no superstar egos.
We listen and respect one another and consciously collaborate to ensure that we create the best possible solution for our clients. People do business with people they know and trust and that applies to colleagues as much as it does clients.  "]
                         }

                        {:name ["Alex Brimmicombe"]
                         :memberid ["abrimmicombe"]
                         :profilepics {:hipster ["/img/teampics/abrimmicombe_hipster.jpg"]
                                       :everyday ["/img/teampics/abrimmicombe_everyday.jpg"] }
                         :Role ["Architectural Graduate"]
                         :contact ["alex@solariarchitects.com"]
                         :how [["In mid 2015 the stars seemed to align. I was lucky enough to be taken on board by the team at Solari Architects. Before this, I spent ten months in a smaller Wellington firm after completing my Master’s degree at Victoria University. My final year of study focussed on safety through spatial design and creating future proof public architecture."]
                               ["Upon meeting James I could see that I had come to the perfect place to advance my skills and develop my knowledge as an architectural graduate. A personal interest in contemporary residential and commercial architecture is being further developed by being a part of the Solari team."]
                               ]
                         :goals [""]
                         :advice ["My favourite part of working at Solari Architects is: The supportive environment, vast range of knowledge and the exquisite coffee"]
                         :outside ["You’ll find me getting dangerously airborne on a bmx or mountain bike, taking blurry photos, or working under an impractically low car."]
                         :polaroid ["I love the supportive environment, vast range of knowledge, and the exquisite coffee at Solari Architects! "]
                         :texttitle ["SOLARI SAYS"]
                         :textpara ["Professional doesn't have to be serious."]
                         }

                        {:name ["Molly Marshall"]
                         :memberid ["mmarshall"]
                         :contact ["molly@solariarchitects.com"]
                         :profilepics {:hipster ["/img/teampics/mmarshall_hipster.jpg"]
                                       :everyday ["/img/teampics/mmarshall_everyday.jpg"] }
                         :Role ["Architectural Graduate"]
                         :how [["I Joined the Solari team full time in 2015. Before that I was working part time with them whilst completeing my Master’s thesis at Victoria university. I’m enjoying the opportunity to be involved in all areas of the architectural process. "]
                               ]
                         :goals [""]
                         :advice ["Away from work you’ll fine me: playing social netball and café hopping around Wellington."]
                         :outside ["you’ll find me playing social netball and café hopping around Wellington."]
                         :polaroid ["You’ll find me playing social netball and café hopping around Wellington."]
                         :texttitle ["SOLARI SAYS"]
                         :textpara ["Less ego, more client."]
                         }
                        ]
                    })

(def contact-data {:text {:bold ["We don't have a giant boardroom table but we do have wine glasses, a beer opener and a coffee machine - which we think make a good starting point to any meeting."]
                          :paragraph [""] }
                   :info [{:bold ["Visit, drink, chat, bounce ideas here:"]
                           :paragraph [["Level 1"]["13-15 Adelaide Road"]["Mt Cook"]["Wellington"]]}
                          {:bold ["Call, talk, joke, debate, ask here: "]
                           :paragraph [["+64 4 9744562"]]}
                          {:bold ["Email jokes, work or gifs here: "]
                           :paragraph [["hello@solariarchitects.com"]]}]})


(def sorted-state {:name true :date false})

(def all-data
  (atom {:home-page-data home-page-data
         :residential-data residential-data
         :multi-unit-data multi-unit-data
         :commercial-data commercial-data
         :for-you-data for-you-data
         :for-architects-data for-architects-data
         :from-us-data from-us-data
         :your-career-data your-career-data
         :projects-data projects-data
         :process-data process-data
         :faqs-data faqs-data
         :the-team-data the-team-data
         :contact-data contact-data
         :all-projects [pr/project-01 pr/project-02 pr/project-03 pr/project-04 pr/project-05 pr/project-06
                        pr/project-07 pr/project-08 pr/project-09 pr/project-10 pr/project-11 pr/project-12]
         :sorted-state sorted-state
         :twitter-data []}))


(defresource all-data-resource
             :service-available? true
             :allowed-methods [:get :put]
             :handle-method-not-allowed  "Method not allowed"
             :handle-ok (fn [context] @all-data)
             :put! (fn [ctx] (reset! all-data (:all-data (:params (:request ctx)))))
             :available-media-types ["application/edn"])

(defresource twitter-resource
             :service-available? true
             :allowed-methods [:get]
             :handle-method-not-allowed "Method not allowed"
             :handle-ok (fn [context]
                          {:twitter-data
                           (statuses-user-timeline :oauth-creds my-creds :params {:screen-name "SolariArch" :count 100})})
             :available-media-types ["application/edn"])

(def map1 '({:one "one" :two "two"}))
(def map2 '({:three "three" :four "four"}))

(into (into [] map1) map2)
(into map1 map2)

(conj (vec map1) (vec map2))




