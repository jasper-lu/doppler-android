# AI Dataset Management — Target Company Map

*Research output: 158 AI companies investigated for ML ops / dataset management business development. Mid-2026.*

---

## Executive summary

We mapped **158 AI companies** across foundation labs, coding/agent platforms, voice/audio/video gen, robotics, autonomous vehicles, defense, bio/materials, vertical agents (legal/healthcare/finance), eval-heavy product companies, and international AI labs.

**Headline finding**: 39 companies are **Tier A HOT** — they have currently-open job descriptions or public technical posts that read like a product spec for dataset/eval management tooling. Outreach to this cohort should reference the JD verbatim.

Beyond Tier A, ~30 more are Tier B HOT (active training + real pain, but the pitch needs more shaping), and ~60 are WARM (investigate, but expect friction — established internal infra, niche schemas, or build-DNA cultures). The remainder are COLD or direct competitors.

**Critical context**: at least 11 companies in this space are already selling overlapping products — Mistral (Forge), Arcee, Palantir (AIP Evals), Applied Intuition (Synthetic Datasets), Mercor, Scale AI, Surge, Snorkel, Argilla, **DatologyAI** (very direct), **OpenPipe**, **Predibase** (Rubrik), **Bespoke Labs**. Position carefully.

A new category emerged during research: **RL Environments** companies (Mechanize, Deeptune, AfterQuery, Prime Intellect, etc.). These are simultaneously potential customers, partners, and competitors depending on positioning — they warrant a strategic decision before outreach.

---

## How to use this document

**Tier definitions**:
- **HOT-A** = Open JD or public statement explicitly maps to a dataset/eval management product. Call this week.
- **HOT-B** = Active training/fine-tuning with real eval surface, but pitch needs more shaping. Call this quarter.
- **WARM** = Real workload but friction (mature internal tooling, niche schemas, or build-DNA culture). Investigate; expect a longer cycle.
- **COLD** = Doesn't train, has been acquired, has pivoted away, or is too large/Alphabet-bound.
- **COMPETITOR** = Already sells overlapping product; do not pitch.

**Build posture** captures whether they: *Pretrain* foundation models from scratch · *Fine-tune* on top of an open or frontier base (SFT/RL) · *Wrap+Eval* (mostly API harnessing with heavy eval surface) · *Mixed*.

**Method** captures SFT (supervised fine-tuning), RL (any flavor — RLHF, RLAIF, RLVR, RLCEF, multi-turn agent RL), Diffusion, Eval-only, or Multi-modal/custom.

---

## Six patterns to internalize before outreach

1. **Buying-trigger phrases.** These six phrases recur in JDs and engineering blogs across the HOT list — use them as your weekly job-board crawl keywords:
   - *"data pipelines and evaluation systems"*
   - *"training and dataset platform"*
   - *"evaluation framework / harness"*
   - *"data engine"* or *"data flywheel"*
   - *"RL environments"*
   - *"human-in-the-loop annotation"*

2. **Forced-retrain triggers are the strongest near-term signal.** Three flavors:
   - *Regulatory/IP*: Suno (Warner licensing settlement), Synthesia (consent governance), Stability AI (Getty lawsuit), Mach Industries (new CASO greenfield decisions)
   - *Product launch with explicit data dependency*: 1X NEO, Apptronik fleet, Figure Project Go-Big, Pika data engine
   - *Acquisition integration*: Spellbook + Library, Mintlify + Trieve, Captions → Mirage rebrand

3. **Multi-tenant per-customer fine-tuning is the highest-volume lane.** Cresta, Sierra, Decagon, Glean, Intercom Fin, Zendesk, Eve Legal, Crosby, Tennr, EvenUp all run N pipelines × N eval sets across enterprise customers. Multi-tenant dataset/eval governance is a wedge none of them have solved.

4. **Bio came back universally WARM, not HOT.** Bio data schemas (FASTA, mmCIF, multi-omics, microscopy) don't fit generic tooling, and these orgs have biology PhDs who'll resist generic dataset platforms. The exceptions (Profluent HOT) are the ones publicly admitting curation is the bottleneck and hiring data infra. Strategy: pitch the central-FM teams, not federated/wet-lab sides; consider a bio-vertical SKU.

5. **Eval-heavy product cos** (Figma, Canva, Datadog, Intercom, Zendesk, Ramp) are an under-appreciated, *higher-volume* cohort. Lower individual deal size than foundation labs, but: fewer competitors targeting them, established procurement budgets, and public regression incidents create executive sponsorship.

6. **Cross-source signal is the strongest filter.** Companies that showed up at 2+ GPU providers' customer pages (Physical Intelligence, Cartesia, Iambic, Cursor, Jua, Genesis Therapeutics) are highest-confidence training operations.

---

## TIER A HOT (call this week / quarter)

### Voice / Audio / TTS
| Company | Build | Method | Sharpest evidence |
|---|---|---|---|
| **ElevenLabs** | Pretrain TTS family | SFT | Open *Research Engineer* JD specs "TTS data versioning + auto-train/eval routines" verbatim; Voice Data Partnerships program soliciting human conversational audio |
| **Cartesia** | Pretrain Sonic-3, Mamba-3, Ink-Whisper | SFT | Open *ML Engineer, Data* JD: "design and build large-scale datasets…multilingual annotation"; Together customer; Crusoe just tripled cluster |

### Video / Avatar / Image gen
| Company | Build | Method | Sharpest evidence |
|---|---|---|---|
| **Pika** | Pretrain video diffusion | Diffusion + SFT | Open *Senior Research Engineer (Data)* JD |
| **Luma** | Pretrain Ray3 / Dream Machine | Diffusion | ML Research Engineer JDs explicitly say "design data pipelines — sourcing, scraping, filtering, post-processing, de-duplicating, and versioning" |
| **fal** | Trains/serves AuraFlow + customer fine-tunes | Diffusion + SFT | lambda.ai/case-studies/fal — multi-node H100 bursts |
| **Decart** | Pretrain MirageLSD real-time AI video | Diffusion + SFT | Crusoe blog — millions of hours of video, thousands of GPUs |
| **Moonvalley** | Pretrain "Marey" licensed AI video for Hollywood | Diffusion | CoreWeave portfolio co; explicitly licensed-data approach = dataset-heavy |
| **Lemon Slice** (YC W24) | Pretrain custom 20B video diffusion transformer | Diffusion | modal.com Lemon Slice case study; parallel eval of checkpoints |
| **Hedra** | Pretrain AI character video | Diffusion + SFT | Together customer — 300x compute growth on Together |

### Foundation labs / new model trainers
| Company | Build | Method | Sharpest evidence |
|---|---|---|---|
| **Zyphra** | Pretrain ZAYA1-8B reasoning MoE | SFT + RL | zyphra.com/post/zaya1 — 1,024 AMD MI300X GPUs |
| **Slingshot AI** | Pretrain mental-health LLM "Ash" (32B–235B) | SFT + safety alignment | nebius.com/customer-stories/slingshot-ai — clinical therapy data |
| **Recraft** | Pretrain 20B image-gen FM from scratch | Diffusion | nebius.com/customer-stories — PyTorch + Kubeflow on Nebius |
| **Jua** | Pretrain EPT-2 climate/weather FM | Multimodal physics | Cross-confirmed Nebius + Crusoe; petabyte-scale paired with VAST |
| **Higgsfield AI** | Pretrain image/keyframe video diffusion | Diffusion | $130M Series A; HGX B200 cluster |
| **SynthLabs** | Post-training/alignment research; "Big Math" dataset | RLHF + RLAIF | nebius.com/customer-stories/synthlabs |
| **Fundamental** | Pretrain "Nexus" tabular FM (non-transformer, deterministic) | Custom | $255M Series A, Feb 2026 |
| **Reflection AI** | Pretrain LLMs + RL (Asimov coding agent) | Heavy RL + reward modeling | $2B raise; ex-DeepMind Laskin/Antonoglou |
| **Prime Intellect** | Decentralized training + Environments Hub; INTELLECT-3 106B MoE | RL + SFT | Cross-confirmed Nebius + X coverage Nov 2025 |

### Coding / agent companies
| Company | Build | Method | Sharpest evidence |
|---|---|---|---|
| **Sourcegraph** | Per-language fine-tune of Cody | SFT | Open *Head of ML Infra* + 4 ML ICs; archived public eval repo Dec 2025 → rebuilding internal eval suite |

### Robotics / humanoid
| Company | Build | Method | Sharpest evidence |
|---|---|---|---|
| **1X Technologies** | Pretrain Redwood VLA + 1X World Model | SFT + policy learning | Open *Research Engineer, Data Infrastructure* JD: "design data engine that uploads fleet data, makes it queryable, ensures high-quality pipelines" |
| **Apptronik** | Pretrain via NVIDIA GR00T + custom RL | RL + sim-to-real | Open *Data Platform Engineer* (Greenhouse 5978948004); $520M Series A-X extension |
| **Sanctuary AI** | Pretrain Carbon control system | SFT + RL (Isaac Lab) | Dedicated *Software Engineering — Data Collection Infrastructure* org + MLOps Engineer JD |
| **Sereact** | Pretrain Cortex 2 robot FM | SFT + RL | $110M Series B April 2026; 1B+ real production picks |
| **Dyna Robotics** | Pretrain DYNA-1 robot FM | SFT | Alluxio training pipeline |

### Autonomous vehicles
| Company | Build | Method | Sharpest evidence |
|---|---|---|---|
| **Waabi** | Pretrain Waabi Driver in neural simulator | SFT + neural sim | Open Lever *SWE Data Platform* + *ML Datasets & Pipelines* Toronto; $1B raise Jan 2026 |

### Defense / intel
| Company | Build | Method | Sharpest evidence |
|---|---|---|---|
| **Vannevar Labs** | Fine-tune Mistral 7B for 79+ languages | SFT | Open *ML Platform Mgr* + *MLOps* + *ML Eng Agents*; ZenML case study (Tagalog 65 → 76% accuracy via fine-tune) |

### Vertical legal
| Company | Build | Method | Sharpest evidence |
|---|---|---|---|
| **Harvey** | Co-trained custom case-law model with OpenAI; multi-FM router | SFT + pairwise preference | *Applied Legal Researcher* JD: "Building proprietary benchmarks and datasets to evaluate models and model systems" + fine-tuning; BigLaw Bench / LAB shipping 75K+ rubric criteria, 1,200+ tasks |
| **Crosby** | Per-customer fine-tune | SFT continuous | Open *Head of Data* JD (builtinnyc 8919030); CTO public on Sequoia *Training Data*: "EDGAR is overutilized" |
| **Eve Legal** | Per-firm fine-tune | SFT | ML Engineer JD: "owning training pipelines for LLMs from data curation to evaluation"; multi-tenant SME-curated data |

### Vertical healthcare
| Company | Build | Method | Sharpest evidence |
|---|---|---|---|
| **Tennr** | Fine-tune RaeLM VLM on 4M+ medical documents | SFT | YC ML Engineer JD + ML Ops req: "evaluation frameworks…data pipelines and ML infrastructure" |

### Bio / materials
| Company | Build | Method | Sharpest evidence |
|---|---|---|---|
| **Profluent** | Pretrain ProGen3 (46B / 1.5T tokens) | SFT | 4 active data-infra reqs (greenhouse.io/profluent); CEO Ali Madani public statement: "curation and alignment is a central problem"; $106M Nov 2025 |
| **Orbital Materials** | Pretrain Orb interatomic potential | GNN + SFT | Open *Lead AI Engineer* + Curie platform; Pixel Scientia podcast on infra pain |
| **Noetik** | Pretrain OCTO transformer for cancer biology | Multimodal SFT | GSK $50M deal Jan 2026; Latent Space podcast guest |

### Eval-heavy product
| Company | Build | Method | Sharpest evidence |
|---|---|---|---|
| **Figma** | Fine-tune embedding / visual search | SFT + nightly contractor evals | First Round "Art of Evals" piece; AI Infrastructure JD: "data pipelines to collect high quality data, and evaluation systems"; Apple Weather regression (Jul 2024) is the case study they'll recognize |
| **Canva** | Pretrain Phoenix / Magic Layers / Canva Design Model | SFT + Diffusion + RLHF | Open *ML Engineer — Training & Dataset Platform* JD; 100+ researchers in CORE post-Leonardo |
| **Datadog** | Pretrain Toto time-series FM; Bits AI agentic | SFT + extensive eval | Open *Sr SWE AI Platform — Evaluation & Annotation* (careers.datadoghq.com/detail/7050512); public Bits AI eval-platform blog ("we built it from scratch") |
| **Zendesk** | Train proprietary CX FMs + per-customer LoRA via Ultimate | SFT (Ultimate adaptive) | Open *Sr ML Eng — Agentic AI Platform* (R32450): "A/B Testing & Evaluation benchmarking platform" |

### Eval-native (consider as customer + ecosystem partner)
| Company | Build | Method | Sharpest evidence |
|---|---|---|---|
| **Vals.ai** | Builds 3rd-party AI evals (legal/finance/accounting) | Eval-native | Together customer; eval API workload |
| **Letta** (ex-MemGPT) | Skill Learning + Context Repositories (versioned agent memory) | Agent infra | letta.com — directly adjacent to dataset/eval mgmt |

---

## TIER B HOT (call this half)

Active training + eval pain; pitch needs sharpening.

### Coding agents
- **Cursor (Anysphere)** — Pretrain Composer 2 MoE from scratch; heavy real-time RL + SFT. JDs: *SWE, Agent Eval & Quality* ("curated datasets, scorers/judges, regression alerts, clustering failure modes"); Composer 2 Tech Report; SpaceX compute partnership.
- **Windsurf / Cognition (Windsurf side)** — Pretrain SWE-1/1.5/grep family. Multi-turn RL + SFT. Open *Research, Post-Training* JD; SWE-grep blog cites "Cognition CodeSearch Eval."

### Customer support / agentic SaaS
- **Cresta** — Fine-tune LoRA per customer; thousands of customer-specific datasets. Fireworks case study + "Data-Generation Agent" blog.
- **Sierra** — Post-train + fine-tune at scale; authors of tau-bench / tau2/3-bench. "Constellation of models" blog; Agent Data Platform productized; $950M raise May 2026.
- **Decagon** — SFT + RL on top of FMs; >80% of traffic runs on own-trained models. "Introducing Decagon Labs" blog; ML Research Eng JD.
- **Intercom (Fin)** — Train Fin Apex vertical model + per-customer procedures. SFT + retrieval fine-tuning. "Announcing Fin Apex" blog; 60+ AI Group; Berlin expansion.

### Vertical
- **Glean** — Train custom embedding model per customer (BERT continued pretraining + contrastive). Open *MLE, LLM Evals & Observability* JD; Jason Liu blog on per-customer embedding.
- **Hippocratic AI** — Pretrain 22-model Polaris constellation (4.2T params total). SFT + safety alignment + 6,234-clinician RWE-LLM evaluation. Polaris 3.0 announcement; NVIDIA HyperPod case study.
- **EvenUp** — Fine-tune Piai on millions of medical records; SFT + continuous learn-from-edits with 100+ in-house SMEs. Staff MLE JD.
- **Norm Ai** — Fine-tune smaller LLMs + decision-tree agents. Stanford Law paper (Nay 2025); 1,456-question attorney-curated benchmark.
- **Rilla** — Train custom ASR for in-person field-sales audio. Largest dataset of in-person sales conversations.
- **Genesis Therapeutics** — Pretrain "Pearl" protein-ligand cofolding model (claimed to surpass AlphaFold3). Cross-confirmed Lambda + X coverage.
- **Distyl AI** — "Distillery" platform; per-client custom enterprise agents; OpenAI alliance.

### Voice / Avatar / Video
- **Suno** — Pretrain music gen; pivoting to fully licensed retraining post-Warner settlement. ML Infra Engineer JD on Dagster; Senior Data Infra JD. *Forced-retrain trigger.*
- **Synthesia** — Pretrain EXPRESS-2 stack (3 models including their own Express-Eval evaluation model). Diffusion + custom eval. Consent-metadata governance for every avatar.
- **HeyGen** — Pretrain Avatar V; 5-stage training curriculum on 50M raw videos. SFT + RLHF. Public 25-stage data pipeline tech report.
- **Captions / Mirage** — Pretrain UGC video FM. Sept 2025 rebrand to research lab; *SWE, ML Data Platform* JD; "dataset design and collection" research pillar.
- **Tavus** — Pretrain Phoenix family (1→4). Diffusion + SFT. Fresh $40M Series B; multimodal avatar training.
- **PlayHT** — Train conversational/voice AI TTS. Lambda 1-Click Clusters customer.

### Foundation / coding
- **EleutherAI** — Open-source LLM research collective. GPT-NeoX-20B trained on CoreWeave; The Pile, Pythia datasets. Dataset-native organizational identity.
- **Bit192** — Japanese 20B LLM lab. CoreWeave customer; multilingual training pain.
- **Tarteel AI** — Quranic recitation/speech AI. CoreWeave customer; specialized Arabic audio data.
- **Odyssey** — World-models AI lab; "Explorer" consumer app; Crusoe Managed K8s design partner.
- **Gradient AI** — 1M-context Llama-3 variants (8B + 70B). Crusoe + RingAttention.
- **Genspark** — RFT-trained 32B agent model. Fireworks case study.
- **Vercel (v0)** — Reinforcement fine-tuning for code-gen. Fireworks case study.

### Robotics
- **Figure** — Pretrain Helix VLA; building Project Go-Big mega-dataset (Brookfield 100K-unit human video partnership). SFT + teleop + passive video.

### Eval-heavy product
- **Ramp** — Production fine-tune (receipt-to-JSON model on Modal). SFT + distillation + SME evals. Modal case study; "What it takes to build AI agents at scale" blog.
- **Substack** — Migrated all ML training (spam, recs, transcription, sentiment, image gen) to Modal. Modal case study.
- **Zencastr** — Trains audio models; 1,500 concurrent GPUs for feature extraction on Modal.
- **Contextual AI** — "RAG 2.0" enterprise platform; trains retrieval + generation models. Modal case study.
- **Superhuman** — Dozens of fine-tuned embedding models. Baseten customer.
- **Scaled Cognition** — Custom agent FMs powering Genesys (8K customers). Baseten customer.
- **OpenEvidence** — Medical Q&A; billions of fine-tuned LLM calls/week. Baseten customer.

### Defense (with on-prem story required)
- **Tzafon** — Agentic ML lab; trained North Star 0.1 (Gemma-27B + UI-TARS-7B) on Voltage Park.

### Eval-only / RAG
- **Protege AI** — "ShieldLlama" risk-posture foundation model; continued training, LoRA fine-tunes. Together customer.
- **InVideo** — AI video generation/editing. Together customer; Blackwell test drive.

---

## RL Environments cluster (special category)

A new cohort of well-funded startups whose entire business is *building RL training environments and traces* for frontier labs. Each one runs heavy data generation + eval-trace curation pipelines. Decide your positioning (customer / partner / competitor) before outreach.

| Company | What they do | Funding signal | Lead |
|---|---|---|---|
| **Mechanize** (mechanize.work) | RL environments lab | Friedman / Gross / Collison / D'Angelo | HOT |
| **Deeptune** (deeptune.ai) | High-fidelity RL gyms (accountant / CS / DevOps) | $43M Series A a16z, Mar 2026 | HOT |
| **AfterQuery** (afterquery.com) | YC; RL envs on real APIs / MCP servers | $31M | HOT |
| **BenchFlow** (benchflow.ai) | SkillsBench / ClawsBench env lab | n/a | WARM |
| **Calaveras** | Code RL envs | Seed | WARM |
| **RunRL** (YC) | RL-as-a-service | Seed | WARM |
| **Cua** | Computer-use RL envs | n/a | WARM |
| **HUD** (hud.ai) | RL tooling / env aggregator | n/a | WARM |
| **Bespoke Labs** (bespokelabs.ai) | Curated reasoning datasets (Open Thoughts) | Seed | PARTNER/COMP |

---

## WARM (~60) — investigate, expect friction

### Foundation labs / new model trainers (build-DNA)
Sakana AI · Liquid AI · Black Forest Labs · Ideogram · Runway · World Labs · Magic.dev · Cognition (parent) · AI21 Labs · Upstage · Sarvam · Stability AI · Niantic Spatial · Reka · Inception Labs · Goodfire · H Company · OpenHands · Reflection AI (cooler — see Tier A) · Ema

### Bio / materials
EvolutionaryScale · Iambic Therapeutics · Chai Discovery · Inceptive · Insitro · Tempus · Owkin · Cradle · Latent Labs · Periodic Labs · Atomic AI · Recursion · Helical · Converge Bio · xAID · SieveStack · TrialHub · SphinxBio · Biolexis Therapeutics · Aneta

### Robotics / AV
Physical Intelligence · Skild AI · Generalist AI · Kodiak · Twelve Labs · Spline · Positronic Robotics · Matrice.ai · Radical AI

### Defense
Helsing · Shield AI · Saronic · Mach Industries

### Vertical / product
Hebbia · Replit · Abridge · Writer · Rad AI · Notion · Atlassian (Rovo) · Mintlify · Spellbook · Bland · Inworld · Handshake · Civitai · Glam Labs · KRNL/Loopsie · Gendo · Coframe · OpenArt · Krea · AlliumAI · NovelAI · InstaHeadshots · LiveEO · Imazon · Rainforest Connection · SewerAI · Attentive · Sully.ai · PicnicHealth · Dippy AI · Scatter Lab · Neuralift AI · Descriptor.AI · Wubble · GenreX · Paninski Lab · TinyLlama · Validia · Lovable · Dataworkz

---

## COLD / COMPETITOR (~35) — deprioritize or avoid

### Direct competitors (do NOT pitch)
| Company | Reason |
|---|---|
| **Mistral** | Just productized Forge as competing dataset/eval product |
| **Arcee AI** | Sells training-as-a-service; partnered with DatologyAI |
| **Palantir (AIP)** | AIP Evals shipped as competing customer-facing product |
| **Applied Intuition** | Sells Synthetic Datasets product |
| **Mercor** | $1B run-rate expert-annotation marketplace |
| **DatologyAI** | Pure dataset curation play — generated 10T synthetic tokens for Arcee |
| **OpenPipe** | Managed LLM fine-tuning service |
| **Predibase** (Rubrik) | RFT platform |
| **Bespoke Labs** | Curated dataset platform — possible partner |
| **Voltron Data** | Apache Arrow / GPU-native data processing |

### Mature internal infra / build-DNA culture
Cohere · Poolside · Imbue · Nous Research · Wayve · Nuro · Anduril · Isomorphic Labs

### Acquired / absorbed
Sana (Workday) · CSM (Google) · Silo AI (AMD) · Aleph Alpha (Cohere) · AI21 Labs (Nvidia pending) · Covariant (Amazon)

### Wraps APIs / no training surface
Linear · Truewind · Promethean AI

### Strategic pivot
Krutrim (paused models May 2026)

---

## Cross-cut: training vs evaluation focus

| Bucket | Count | Examples |
|---|---|---|
| **Pretrain own FMs from scratch** | ~50 | Cursor, Hippocratic, ElevenLabs, Cartesia, Suno, Pika, Luma, Runway, BFL, Tavus, Synthesia, HeyGen, Magic, Poolside, Liquid, Reka, Sakana, Mistral, Cohere, AI21, Upstage, Sarvam, Stability, Twelve Labs, Physical Intelligence, Skild, 1X, Figure, Apptronik, Sanctuary, Generalist, Wayve, Waabi, EvolutionaryScale, Profluent, Chai, Iambic, Insitro, Atomic, Orbital, Periodic, Niantic Spatial, Datadog (Toto), Canva, Inworld, Zyphra, Slingshot, Recraft, Jua, Higgsfield, Decart, Moonvalley, Lemon Slice, Sereact, Dyna, Reflection, Noetik, Fundamental |
| **Fine-tune on top of frontier/open base** | ~50 | Harvey, Glean, Sourcegraph, Cresta, Sierra, Decagon, Writer, Abridge, Rad AI, Arcee, Nous, Rilla, Bland, Tennr, EvenUp, Norm Ai, Eve Legal, Crosby, Intercom Fin, Zendesk, Ramp, Notion, Figma, Atlassian, Vannevar, Helsing, Shield AI, Anduril, Owkin, Tempus, Cradle, Latent Labs, Inceptive, Spline, Hedra, Genspark, Vercel v0, Superhuman, Substack, Zencastr, Contextual AI, Scaled Cognition, OpenEvidence, Tzafon, fal, PlayHT, Genesis Therapeutics, Distyl |
| **Mostly wrap APIs + heavy eval / RAG only** | ~12 | Hebbia, Replit, Linear, Sana, Mintlify, Spellbook, Truewind, Promethean, Atlassian Rovo (partial), Lovable, Dataworkz |
| **RL-heavy** | ~15 | Cursor (real-time RL), Cognition (multi-turn RL), Reflection AI, Helsing (Centaur), Shield AI (Forge), Anduril, Imbue, 1X, Apptronik, Physical Intelligence, Decagon (post-train RL), Periodic Labs, Prime Intellect, Vercel v0 (RFT), Genspark (RFT) |
| **Diffusion-specific** | ~13 | BFL, Ideogram, Stability, Pika, Luma, Runway, Suno (audio), Synthesia, HeyGen, Tavus, Spline, Lemon Slice, Decart, Moonvalley |
| **Eval-native (sells evals)** | 2 | Vals.ai, Letta (adjacent) |

---

## Sourcing channels worth re-running weekly

In order of yield observed during this research:

1. **Job-board diff** (Greenhouse + Ashby + Lever public APIs) — query for the 6 trigger phrases listed above. New hits = freshest buying windows. Weekly cadence.
2. **GPU/compute customer pages** — Modal, Crusoe, Nebius, Voltage Park, Together / Fireworks / Baseten produced the richest mid-sized signal. CoreWeave skews toward mega customers. Lightning AI and Runpod yielded thin returns; deprioritize.
3. **arXiv affiliation crawl** — filter cs.LG / cs.CL papers from last 6 months by non-academic affiliation + keywords (fine-tuning, post-training, evaluation, RLHF/RLVR).
4. **Funding-announcement RSS** (Crunchbase / Strictly VC) — 90-day window after a round closes is the strongest "buy something now" trigger.
5. **VC portfolio sweeps** — a16z, Sequoia, Index, Bessemer, Lightspeed AI portfolios; YC batches W23 → present.
6. **Conference sponsor + speaker rosters** — NeurIPS / ICML / ICLR / COLM / RLC; AI Engineer World's Fair; Latent Space podcast guest list; Sequoia "Training Data" podcast guests.

---

## Top 10 to call this week (sharpest fit + best timing)

| # | Company | Sharpest trigger to cite in cold email |
|---|---|---|
| 1 | **Harvey** | *Applied Legal Researcher* JD literally specs benchmark + dataset construction + fine-tuning |
| 2 | **ElevenLabs** | Research Engineer JD specs "TTS data versioning + auto-train/eval routines" |
| 3 | **Cartesia** | Open *ML Engineer, Data* JD; Crusoe just tripled their cluster size |
| 4 | **Canva** | Open *ML Engineer — Training & Dataset Platform* req on AI Platform group |
| 5 | **1X Technologies** | Open *Research Engineer, Data Infrastructure* JD pre-NEO consumer launch |
| 6 | **Apptronik** | Open *Data Platform Engineer* + $520M extension |
| 7 | **Datadog** | Open *Sr SWE AI Platform — Evaluation & Annotation* + Toto FM in production |
| 8 | **Figma** | *AI Infrastructure* JD + public Apple Weather regression precedent |
| 9 | **Crosby** | Open *Head of Data*; CTO public on EDGAR limitations |
| 10 | **Vannevar Labs** | *ML Platform Mgr* + 79-language fine-tuning ZenML case study |

---

## Recommended action plan

1. **This week** — Outreach to the 10 names above. Cold-email template should quote the open JD verbatim.

2. **This month** — Tier A remaining (29 more names). For each, identify the specific hiring manager via LinkedIn before sending. Lead with the buying trigger you uncovered (e.g., for Synthesia → consent-metadata; for Suno → Warner-licensed retrain; for Sierra → tau-bench per-tenant golden datasets).

3. **This quarter** — Tier B (~30 names). Pitch needs more shaping per company; use the trigger keywords above to filter.

4. **Strategic forks before scaling**:
   - **Bio SKU?** Bio cluster won't bite a generic pitch — decide on protein/structure-aware schemas before pursuing the 16 bio leads.
   - **GovCloud / IL5 story?** Required to convert Vannevar at any depth; required for Helsing / Shield / Saronic / Anduril at all.
   - **Eval-only positioning?** ~12 companies (Hebbia, Replit, Notion, Mintlify, Spellbook, etc.) only buy if your product cleanly separates "eval / RAG curation" from "training data."
   - **RL Environments positioning?** Decide whether you're building above Mechanize / Deeptune / AfterQuery / Prime Intellect (consume their RL traces into your dataset platform) or alongside them.

5. **Avoid entirely** — Mistral, Arcee, Palantir, Applied Intuition, Mercor, Scale, Surge, Snorkel, Argilla, DatologyAI, OpenPipe, Predibase, Bespoke Labs (competitors). Sana, CSM, Silo, Aleph, AI21, Covariant (acquired/absorbed). Linear, Truewind, Promethean (no training surface).

6. **Maintain a sheet, not a snapshot.** Columns: `company | source | funding | headcount | open ML roles | training signal | triage score | tier | last touched`. The sheet — refreshed weekly via the sourcing channels above — is the real asset.

---

## Methodology note

108 companies were investigated in waves 1–5 across foundation labs / coding / customer support / voice-video / robotics / AV / defense / bio / vertical legal-healthcare-finance / eval-heavy product / international. 50 more companies were surfaced in wave 6 via parallel scrapes of CoreWeave, Lambda, Crusoe, Nebius, Voltage Park, Modal, Anyscale, Lightning AI, Runpod, and combined Together / Fireworks / Baseten customer pages, plus a Twitter/X engagement-network crawl of prominent AI training accounts. Each company received an independent research pass covering its careers page, engineering blog, technical reports, and public posts from team members. Citations are publicly accessible URLs.

158 companies total. Bandwidth permitting, channels worth running next: GPU customer pages for Foundry / Together-Cloud-internal / Baseten 2026 cohort; HuggingFace organization-upload deltas; weekly Greenhouse / Ashby keyword diff.
