// "use client"

// import { Radar, RadarChart, PolarGrid, PolarAngleAxis, ResponsiveContainer } from "recharts"
// import { Card } from "@/components/ui/card"

// interface RadarChartProps {
//   data: {
//     angle: number
//     stability: number
//     speed: number
//   }
// }

// export function AnalysisRadarChart({ data }: RadarChartProps) {
//   const chartData = [
//     { subject: "Speed", value: data.speed },
//     { subject: "Stability", value: data.stability },
//     { subject: "Angle", value: data.angle },
//   ]

//   return (
//     <div className="space-y-4">
//       <h2 className="text-2xl text-white text-center font-medium">Analysis Graph</h2>
//       <Card className="p-4 sm:p-6 bg-[#1E1E2D] aspect-square relative overflow-hidden">
//         <ResponsiveContainer width="100%" height="100%">
//           <RadarChart 
//             data={chartData}
//             margin={{ 
//               top: 20, 
//               right: 30, 
//               bottom: 20, 
//               left: 30 
//             }}
//             outerRadius="60%"
//           >
//             <PolarGrid gridType="circle" stroke="rgba(255, 255, 255, 0.1)" strokeWidth={1} radialLines={false} />
//             <PolarAngleAxis
//               dataKey="subject"
//               tick={(props) => {
//                 const { x, y, payload } = props
//                 // Speed 텍스트일 경우 위로 올리고, 나머지는 아래로 이동
//                 const dy = payload.value === "Speed" ? -10 : 10
//                 return (
//                   <g transform={`translate(${x},${y})`}>
//                     <text
//                       x={0}
//                       y={0}
//                       dy={dy}
//                       textAnchor="middle"
//                       fill="white"
//                       fontSize={14}
//                       fontWeight={500}
//                     >
//                       {payload.value}
//                     </text>
//                   </g>
//                 )
//               }}
//               axisLine={false}
//               tickLine={false}
//             />
//             <Radar
//               name="Stats"
//               dataKey="value"
//               stroke="#0066FF"
//               strokeWidth={2}
//               fill="#0066FF"
//               fillOpacity={0.15}
//               dot={(props) => {
//                 const { cx, cy } = props
//                 return (
//                   <g>
//                     <circle cx={cx} cy={cy} r={4} fill="white" stroke="#0066FF" strokeWidth={2} />
//                   </g>
//                 )
//               }}
//             />
//           </RadarChart>
//         </ResponsiveContainer>
//       </Card>
//     </div>
//   )
// }



"use client"

import { Radar, RadarChart, PolarGrid, PolarAngleAxis, ResponsiveContainer } from "recharts"
import { Card } from "@/components/ui/card"

interface RadarChartProps {
  data: {
    angle: number
    stability: number
    speed: number
  }
}

export function AnalysisRadarChart({ data }: RadarChartProps) {
  const chartData = [
    { subject: "Speed", value: data.speed },
    { subject: "Stability", value: data.stability },
    { subject: "Angle", value: data.angle },
  ]

  return (
    <div className="space-y-4">
      <h2 className="text-2xl text-white text-center font-medium">Analysis Graph</h2>
      <Card className="p-4 sm:p-6 bg-[#1E1E2D] aspect-square relative overflow-hidden">
        <ResponsiveContainer width="100%" height="100%">
          <RadarChart 
            data={chartData}
            margin={{ 
              top: 20, 
              right: 30, 
              bottom: 20, 
              left: 30 
            }}
            outerRadius="60%"
          >
            <PolarGrid gridType="circle" stroke="rgba(255, 255, 255, 0.1)" strokeWidth={1} radialLines={false} />
            <PolarAngleAxis
              dataKey="subject"
              tick={(props) => {
                const { x, y, payload } = props
                let dx = 0
                let dy = 0

                // 각 텍스트별 위치 조정
                switch (payload.value) {
                  case "Speed":
                    dy = -10
                    break
                  case "Angle":
                    dx = -20
                    dy = 20
                    break
                  case "Stability":
                    dx = 20
                    dy = 20
                    break
                }

                return (
                  <g transform={`translate(${x},${y})`}>
                    <text
                      x={dx}
                      y={0}
                      dy={dy}
                      textAnchor="middle"
                      fill="white"
                      fontSize={14}
                      fontWeight={500}
                    >
                      {payload.value}
                    </text>
                  </g>
                )
              }}
              axisLine={false}
              tickLine={false}
            />
            <Radar
              name="Stats"
              dataKey="value"
              stroke="#0066FF"
              strokeWidth={2}
              fill="#0066FF"
              fillOpacity={0.15}
              dot={(props) => {
                const { cx, cy } = props
                return (
                  <g>
                    <circle cx={cx} cy={cy} r={4} fill="white" stroke="#0066FF" strokeWidth={2} />
                  </g>
                )
              }}
            />
          </RadarChart>
        </ResponsiveContainer>
      </Card>
    </div>
  )
}