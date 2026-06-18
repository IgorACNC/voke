import './KpiCard.css'

interface Props {
  icon: string
  label: string
  value: string | number
  hint?: string
}

export default function KpiCard({ icon, label, value, hint }: Props) {
  return (
    <div className="kpi-card">
      <span className="kpi-icon">{icon}</span>
      <div className="kpi-body">
        <span className="kpi-label">{label}</span>
        <span className="kpi-value">{value}</span>
        {hint && <span className="kpi-hint">{hint}</span>}
      </div>
    </div>
  )
}
