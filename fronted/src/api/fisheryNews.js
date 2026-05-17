import axios from 'axios'

const RSS_SOURCES = [
  {
    name: '农业农村部',
    url: 'https://rsshub.app/gov/moa',
    category: '政策法规'
  },
  {
    name: '水产养殖网',
    url: 'http://www.shuichan.cc/rss.xml',
    category: '国内养殖'
  },
  {
    name: '中国水产频道',
    url: 'http://www.fishfirst.cn/portal.php?mod=rss',
    category: '水产技术'
  },
  {
    name: '中国渔业协会',
    url: 'http://www.fishery.org.cn/rss.xml',
    category: '国内养殖'
  }
]

const CORS_PROXIES = [
  'https://api.codetabs.com/v1/proxy?quest='
]

const NEWS_POOL = [
  {
    title: '2026年水产养殖绿色发展技术推广取得新进展',
    summary: '农业农村部发布最新数据，全国水产绿色养殖面积同比增长12%，池塘循环水养殖技术在全国推广面积突破500万亩。',
    source: '中国水产养殖网',
    category: '国内养殖',
    url: ''
  },
  {
    title: '智能水质监测系统助力对虾养殖增产30%',
    summary: '广东湛江试点应用AI水质监测与自动调控系统，实现溶解氧、pH值、氨氮等关键指标实时监测预警，对虾成活率大幅提升。',
    source: '水产前沿',
    category: '水产技术',
    url: ''
  },
  {
    title: '2026年休渔期延长至四个半月 海洋渔业资源稳步恢复',
    summary: '农业农村部宣布今年休渔期从5月1日起至9月15日，较往年延长15天。近海渔业资源调查显示带鱼、小黄鱼等主要经济鱼类资源量回升明显。',
    source: '中国渔业报',
    category: '政策法规',
    url: ''
  },
  {
    title: '新型饲料配方降低养殖成本15% 减少氮磷排放',
    summary: '中国水产科学研究院研发的低鱼粉环保饲料在罗非鱼养殖试验中取得突破，饲料系数降至1.2以下，氮磷排放减少30%以上。',
    source: '水产技术推广站',
    category: '水产技术',
    url: ''
  },
  {
    title: '厄尔尼诺现象减弱 太平洋金枪鱼捕捞量预期回升',
    summary: '世界气象组织最新报告显示厄尔尼诺现象将在第三季度减弱，中西太平洋金枪鱼捕捞条件改善，预计下半年产量将恢复至正常水平。',
    source: '中国渔业报',
    category: '国际渔业',
    url: ''
  },
  {
    title: '深远海养殖装备"国信1号"完成首季养殖收获',
    summary: '全球首艘10万吨级智慧渔业大型养殖工船完成首批大黄鱼收获，产量突破3000吨，品质达到野生大黄鱼标准的85%以上。',
    source: '中国渔业报',
    category: '国内养殖',
    url: ''
  },
  {
    title: '欧盟更新水产品进口检验标准 出口企业需重点关注',
    summary: '欧盟发布新版水产品进口法规，新增磺胺类等5种兽药残留限量要求，预计2026年9月起实施，建议相关出口企业提前做好检测准备。',
    source: '中国渔业报',
    category: '政策法规',
    url: ''
  },
  {
    title: '微塑料对贝类养殖的影响研究取得新发现',
    summary: '中科院海洋所研究发现微塑料在贻贝体内累积主要集中于鳃组织，通过48小时清水净化可去除90%以上，为贝类净化技术提供了科学依据。',
    source: '水产前沿',
    category: '水产技术',
    url: ''
  },
  {
    title: '全国水产苗种质量监督抽查合格率达96.8%',
    summary: '农业农村部公布2026年第一季度水产苗种质量监督抽查结果，共抽查南美白对虾、草鱼等8个品种，总体合格率较去年同期提高1.2个百分点。',
    source: '中国水产养殖网',
    category: '国内养殖',
    url: ''
  },
  {
    title: '2026年中央一号文件：强化渔业基础设施建设',
    summary: '文件明确提出加强渔港和渔船停泊点建设，推进养殖池塘标准化改造，支持深远海养殖设施装备研发和推广应用。',
    source: '中国渔业报',
    category: '政策法规',
    url: ''
  },
  {
    title: '稻渔综合种养模式在全国推广面积突破四千万亩',
    summary: '全国水产技术推广总站数据显示，稻渔综合种养面积已达4200万亩，年产优质水产品380万吨，带动农户增收效果显著。',
    source: '农业农村部',
    category: '国内养殖',
    url: ''
  },
  {
    title: '我国自主选育南美白对虾新品种通过审定',
    summary: '中国水产科学研究院黄海水产研究所选育的"黄海5号"南美白对虾新品种通过国家审定，生长速度提高18%，抗病力显著增强。',
    source: '中国水产养殖网',
    category: '水产技术',
    url: ''
  }
]

function getDynamicMockNews() {
  const now = new Date()
  const hoursAgo = (h) => {
    const d = new Date(now.getTime() - h * 3600000)
    return `${d.getMonth() + 1}月${d.getDate()}日 ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
  }

  const shuffled = [...NEWS_POOL].sort(() => Math.random() - 0.5)
  const selected = shuffled.slice(0, 6)

  return selected.map((item, index) => ({
    ...item,
    pubDate: hoursAgo(index * 2 + Math.floor(Math.random() * 8)),
    url: item.url || '#'
  }))
}

async function tryFetchRSS(url) {
  for (const proxy of CORS_PROXIES) {
    try {
      const encoded = encodeURIComponent(url)
      const res = await axios.get(`${proxy}${encoded}`, { timeout: 8000 })
      return typeof res.data === 'string' ? res.data : JSON.stringify(res.data)
    } catch (e) {
      continue
    }
  }

  try {
    const res = await axios.get(url, { timeout: 8000 })
    return typeof res.data === 'string' ? res.data : JSON.stringify(res.data)
  } catch (e) {
    return null
  }
}

export async function fetchFisheryNews() {
  const results = []
  let hasRealData = false

  for (const source of RSS_SOURCES) {
    try {
      const text = await tryFetchRSS(source.url)
      if (!text) continue

      const parser = new DOMParser()
      const xmlDoc = parser.parseFromString(text, 'text/xml')

      const items = xmlDoc.querySelectorAll('item')
      if (items.length === 0) continue

      hasRealData = true
      for (const item of items) {
        const title = item.querySelector('title')?.textContent || ''
        const description = item.querySelector('description')?.textContent || ''
        const link = item.querySelector('link')?.textContent || ''
        const pubDate = item.querySelector('pubDate')?.textContent || ''

        const cleanTitle = title.replace(/&amp;/g, '&').replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&#39;/g, "'").replace(/<!\[CDATA\[|\]\]>/g, '')
        const cleanDesc = description.replace(/<[^>]*>/g, '').replace(/&amp;/g, '&').replace(/<!\[CDATA\[|\]\]>/g, '').substring(0, 200)

        if (cleanTitle) {
          results.push({
            title: cleanTitle,
            summary: cleanDesc,
            source: source.name,
            category: source.category,
            url: link,
            pubDate,
            image: ''
          })
        }
      }
    } catch (error) {
      console.warn(`无法获取 ${source.name}:`, error.message)
    }
  }

  if (!hasRealData || results.length === 0) {
    return { items: getDynamicMockNews(), source: 'mock' }
  }

  return { items: results.slice(0, 20), source: 'live' }
}

export async function fetchFisheryNewsByCategory(category) {
  const { items, source } = await fetchFisheryNews()
  return { items: items.filter(item => item.category === category), source }
}

function getMoonPhase(date) {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()

  let yy = year
  let mm = month
  if (mm < 3) {
    yy--
    mm += 12
  }

  const k = Math.floor(365.25 * (yy + 4712)) + Math.floor(30.6 * (mm + 1)) + day - 152.5
  const jd = k + date.getHours() / 24 + date.getMinutes() / 1440

  const daysSinceNew = (jd - 2451550.1) / 29.53058867
  let phase = daysSinceNew - Math.floor(daysSinceNew)
  if (phase < 0) phase += 1

  return phase
}

function getMoonLunarTransit(date) {
  const phase = getMoonPhase(date)
  const transitMinutes = (phase * 24 * 60 + 12 * 60) % (24 * 60)
  return transitMinutes
}

function computeTidesForDate(date, baseAmplitude, baseOffset) {
  const transit = getMoonLunarTransit(date)
  const phase = getMoonPhase(date)

  const springFactor = 1 + 0.4 * Math.cos(phase * 2 * Math.PI)
  const neapFactor = 1 - 0.3 * Math.cos(phase * 2 * Math.PI)
  const amplitude = baseAmplitude * (0.7 + 0.3 * springFactor)

  const lunarDay = 24 * 60 + 50
  const halfLunarDay = lunarDay / 2

  const records = []

  for (let i = 0; i < 4; i++) {
    let tideMinutes
    let isHigh

    if (i === 0) {
      tideMinutes = (transit - baseOffset) % lunarDay
      isHigh = true
    } else if (i === 1) {
      tideMinutes = (transit - baseOffset + halfLunarDay * 0.45) % lunarDay
      isHigh = false
    } else if (i === 2) {
      tideMinutes = (transit - baseOffset + halfLunarDay) % lunarDay
      isHigh = true
    } else {
      tideMinutes = (transit - baseOffset + halfLunarDay * 1.45) % lunarDay
      isHigh = false
    }

    if (tideMinutes < 0) tideMinutes += lunarDay

    const dayMinutes = tideMinutes % (24 * 60)
    const h = Math.floor(dayMinutes / 60) % 24
    const m = Math.floor(dayMinutes % 60)

    const tideHeight = isHigh
      ? 2.5 + amplitude * springFactor
      : 2.5 - amplitude * neapFactor

    records.push({
      time: `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`,
      type: isHigh ? '高潮' : '低潮',
      height: `${Math.max(0, tideHeight).toFixed(1)}m`
    })
  }

  return records.sort((a, b) => a.time.localeCompare(b.time))
}

function getComputedTides(lat) {
  const absLat = Math.abs(lat || 30)
  const baseAmplitude = 0.8 + absLat * 0.06
  const baseOffset = (absLat % 30) * 2

  const now = new Date()
  const result = []

  for (let i = 0; i < 3; i++) {
    const date = new Date(now.getTime() + i * 86400000)
    const dateStr = i === 0 ? '今天' : i === 1 ? '明天' : `${date.getMonth() + 1}月${date.getDate()}日`
    const records = computeTidesForDate(date, baseAmplitude, baseOffset)

    result.push({ date: dateStr, records })
  }

  return result
}

export function fetchTideData(lat = null) {
  const computedTides = getComputedTides(lat)
  return { items: computedTides, source: 'computed' }
}

export async function fetchMarineWeather(lat, lon) {
  try {
    const res = await axios.get('https://marine-api.open-meteo.com/v1/marine', {
      params: {
        latitude: lat,
        longitude: lon,
        current: ['wave_height', 'wave_direction', 'wave_period', 'ocean_current_velocity', 'ocean_current_direction', 'sea_surface_temperature'],
        daily: ['wave_height_max', 'wave_direction_dominant', 'wave_period_max'],
        timezone: 'auto',
        forecast_days: 3
      },
      timeout: 10000
    })

    if (res.data && res.data.current) {
      const current = res.data.current
      const daily = res.data.daily

      return {
        source: 'live',
        waveHeight: current.wave_height || 0,
        wavePeriod: current.wave_period || 0,
        waveDirection: getWaveDirection(current.wave_direction),
        seaTemp: current.sea_surface_temperature || null,
        currentVelocity: current.ocean_current_velocity || 0,
        currentDirection: getWaveDirection(current.ocean_current_direction),
        forecast: daily ? daily.time.slice(0, 3).map((date, i) => ({
          date: formatMarineDate(date),
          waveHeight: daily.wave_height_max[i] || 0,
          waveDirection: getWaveDirection(daily.wave_direction_dominant[i]),
          wavePeriod: daily.wave_period_max[i] || 0
        })) : []
      }
    }
  } catch (error) {
    console.warn('海洋气象API获取失败:', error.message)
  }

  return getMockMarineWeather()
}

function getWaveDirection(degree) {
  if (degree == null) return '未知'
  const directions = ['北', '东北', '东', '东南', '南', '西南', '西', '西北']
  return directions[Math.round(degree / 45) % 8]
}

function formatMarineDate(dateStr) {
  const date = new Date(dateStr)
  const now = new Date()
  const diffDays = Math.round((date - now) / 86400000)
  if (diffDays === 0) return '今天'
  if (diffDays === 1) return '明天'
  if (diffDays === 2) return '后天'
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

function getMockMarineWeather() {
  return {
    source: 'mock',
    waveHeight: 1.2 + Math.random() * 1.5,
    wavePeriod: 4 + Math.random() * 6,
    waveDirection: ['东北', '东', '东南', '南'][Math.floor(Math.random() * 4)],
    seaTemp: 22 + Math.random() * 6,
    currentVelocity: 0.3 + Math.random() * 1.2,
    currentDirection: ['东北', '东', '东南', '南'][Math.floor(Math.random() * 4)],
    forecast: [
      { date: '今天', waveHeight: 1.0 + Math.random() * 1.8, waveDirection: '东南', wavePeriod: 4 + Math.random() * 5 },
      { date: '明天', waveHeight: 0.8 + Math.random() * 2.0, waveDirection: '东南', wavePeriod: 4 + Math.random() * 5 },
      { date: '后天', waveHeight: 0.9 + Math.random() * 2.2, waveDirection: '东南', wavePeriod: 4 + Math.random() * 5 }
    ]
  }
}