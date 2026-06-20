import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts'
import type { PontoCurva } from '../services/dashboardService'

interface Props {
  pontos: PontoCurva[]
}

// Cores derivadas dos tokens — mantidas explícitas porque recharts não lê CSS variables.
const COLOR_INK_SOFT = '#2A2733'
const COLOR_HUSH = '#605C6E'
const COLOR_SPOTLIGHT = '#FFC857'
const COLOR_MOSS = '#4F6B4A'
const COLOR_GRID = 'rgba(15, 14, 23, 0.08)'

export default function CurvaVendasChart({ pontos }: Props) {
  if (pontos.length === 0) {
    return (
      <p
        className="t-meta"
        style={{ color: COLOR_HUSH, textAlign: 'center', padding: '2rem 0' }}
      >
        Ainda não há vendas para gerar a curva.
      </p>
    )
  }
  const data = pontos.map((p) => {
    // p.data vem como "YYYY-MM-DD"; interpretar como data local evita
    // que o fuso horário UTC arraste o dia para trás na exibição.
    const [ano, mes, dia] = p.data.split('-').map(Number)
    const local = new Date(ano, mes - 1, dia)
    return {
      data: local.toLocaleDateString('pt-BR', { day: '2-digit', month: 'short' }),
      ingressos: Number(p.ingressos),
      receita: Number(p.receita),
    }
  })

  const axisStyle = { fill: COLOR_HUSH, fontSize: 11, fontFamily: 'JetBrains Mono, monospace' }

  return (
    <ResponsiveContainer width="100%" height={300}>
      <LineChart data={data} margin={{ top: 12, right: 16, left: 0, bottom: 0 }}>
        <CartesianGrid stroke={COLOR_GRID} strokeDasharray="0" vertical={false} />
        <XAxis dataKey="data" tick={axisStyle} axisLine={{ stroke: COLOR_GRID }} tickLine={false} />
        <YAxis yAxisId="left" tick={axisStyle} axisLine={{ stroke: COLOR_GRID }} tickLine={false} />
        <YAxis yAxisId="right" orientation="right" tick={axisStyle} axisLine={{ stroke: COLOR_GRID }} tickLine={false} />
        <Tooltip
          contentStyle={{
            background: '#0F0E17',
            border: 'none',
            borderRadius: 4,
            color: '#FBF8F2',
            fontFamily: 'Inter Tight, sans-serif',
            fontSize: 12,
          }}
          labelStyle={{ color: COLOR_SPOTLIGHT, fontFamily: 'JetBrains Mono, monospace', fontSize: 11 }}
          itemStyle={{ color: '#FBF8F2' }}
        />
        <Legend
          wrapperStyle={{ fontFamily: 'Inter Tight, sans-serif', fontSize: 12, color: COLOR_INK_SOFT }}
        />
        <Line
          yAxisId="left"
          type="monotone"
          dataKey="ingressos"
          name="Ingressos"
          stroke={COLOR_SPOTLIGHT}
          strokeWidth={2}
          dot={{ r: 3, stroke: COLOR_SPOTLIGHT, fill: '#0F0E17' }}
          activeDot={{ r: 5 }}
        />
        <Line
          yAxisId="right"
          type="monotone"
          dataKey="receita"
          name="Receita (R$)"
          stroke={COLOR_MOSS}
          strokeWidth={2}
          dot={{ r: 3, stroke: COLOR_MOSS, fill: '#0F0E17' }}
          activeDot={{ r: 5 }}
        />
      </LineChart>
    </ResponsiveContainer>
  )
}
