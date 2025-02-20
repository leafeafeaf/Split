// "use client"

// import { useEffect } from "react"
// import ThemeToggle from "@/components/ui/theme-toggle"
// import { UserMenu } from "@/components/user-menu"
// import { NavigationBar } from "@/components/navigation-bar"
// import { AnalysisRadarChart } from "@/components/charts/radar-chart"
// import { ScoreChart } from "@/components/charts/score-chart"
// import { StatsSection } from "@/components/statistics/stats-section"
// import { BowlingStats } from "@/components/statistics/bowling-stats"
// import { HighlightPlayer } from "@/components/video/highlight-player"
// import { useUserStore } from "@/lib/user"
// import { Card } from "@/components/ui/card"

// export default function HomePage() {
//   const { user, isLoading, fetchUser } = useUserStore()

//   useEffect(() => {
//     fetchUser()
//   }, [fetchUser])

//   if (isLoading) {
//     return (
//       <div className="min-h-screen bg-[#161622] p-6 pb-24">
//         <div className="flex justify-between items-center mb-8">
//           <ThemeToggle />
//           <UserMenu />
//         </div>
//         <div className="flex items-center justify-center h-[calc(100vh-200px)]">
//           <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-[#0066FF] border-r-transparent"></div>
//         </div>
//       </div>
//     )
//   }

//   if (!user) {
//     return (
//       <div className="min-h-screen bg-[#161622] p-6 pb-24">
//         <div className="flex justify-between items-center mb-8">
//           <ThemeToggle />
//           <UserMenu />
//         </div>
//         <div className="flex items-center justify-center h-[calc(100vh-200px)]">
//           <Card className="p-6 bg-[#1E1E2D]">
//             <p className="text-white">Failed to load user data. Please try again later.</p>
//           </Card>
//         </div>
//       </div>
//     )
//   }

//   const handleDownload = () => {
//     const link = document.createElement("a")
//     link.href = user.highlight
//     link.download = `highlight-${user.nickname}.mp4`
//     document.body.appendChild(link)
//     link.click()
//     document.body.removeChild(link)
//   }

//   return (
//     <div className="min-h-screen bg-[#161622] p-6 pb-24">
//       <div className="flex justify-between items-center mb-8">
//         <ThemeToggle />
//         <UserMenu />
//       </div>

//       <div className="space-y-6 max-w-md mx-auto">
//         <AnalysisRadarChart
//           data={{
//             angle: user.elbowAngleScore,
//             stability: user.armStabilityScore,
//             speed: user.armSpeedScore,
//           }}
//         />
//         <ScoreChart />
//         <StatsSection
//           scoreStats={[
//             { label: "Average Score", value: user.totalPoseAvgscore.toFixed(1), color: "#2D3EF9" },
//             { label: "High Score", value: user.totalPoseHighscore.toFixed(1), color: "#2D3EF9" },
//           ]}
//           performanceStats={[
//             { label: "Angle", value: user.elbowAngleScore.toFixed(1), color: "#9CB1D1" },
//             { label: "Stability", value: user.armStabilityScore.toFixed(1), color: "#9CB1D1" },
//             { label: "Speed", value: user.armSpeedScore.toFixed(1), color: "#9CB1D1" },
//           ]}
//         />
//         <BowlingStats
//           stats={[
//             { label: "Average Score", value: user.avgBowlingScore.toString(), color: "#ECE929" },
//             { label: "Current Score", value: user.currBowlingScore.toString(), color: "#ECE929" },
//           ]}
//         />
//         <HighlightPlayer videoUrl={user.highlight} onDownload={handleDownload} />
//       </div>

//       <NavigationBar />
//     </div>
//   )
// }






// "use client"

// import axios from 'axios'
// import { useEffect } from "react"
// import dynamic from 'next/dynamic'

// // Dynamic imports to fix the component loading issues
// const ThemeToggle = dynamic(() => import("@/components/ui/theme-toggle"), {
//   ssr: false
// })
// const UserMenu = dynamic(() => import("@/components/user-menu").then(mod => mod.UserMenu), {
//   ssr: false
// })
// const NavigationBar = dynamic(() => import("@/components/navigation-bar").then(mod => mod.NavigationBar), {
//   ssr: false
// })
// const AnalysisRadarChart = dynamic(() => import("@/components/charts/radar-chart").then(mod => mod.AnalysisRadarChart), {
//   ssr: false
// })
// const ScoreChart = dynamic(() => import("@/components/charts/score-chart").then(mod => mod.ScoreChart), {
//   ssr: false
// })
// const StatsSection = dynamic(() => import("@/components/statistics/stats-section").then(mod => mod.StatsSection), {
//   ssr: false
// })
// const BowlingStats = dynamic(() => import("@/components/statistics/bowling-stats").then(mod => mod.BowlingStats), {
//   ssr: false
// })
// const Card = dynamic(() => import("@/components/ui/card").then(mod => mod.Card), {
//   ssr: false
// })

// import { useUserStore } from "@/lib/user"

// interface HighlightPlayerProps {
//   videoUrl: string;
//   nickname: string;
// }

// const HighlightPlayer = ({ videoUrl, nickname }: HighlightPlayerProps) => {
//   const handleDownload = async () => {
//     try {
//       const response = await axios({
//         url: videoUrl,
//         method: 'GET',
//         responseType: 'blob',
//       });

//       const blob = new Blob([response.data], { type: 'video/mp4' });
//       const blobUrl = URL.createObjectURL(blob);
//       const a = document.createElement('a');
//       a.href = blobUrl;
//       a.download = `highlight-${nickname}.mp4`;
//       a.style.display = 'none';
//       document.body.appendChild(a);
//       a.click();
//       document.body.removeChild(a);
//       URL.revokeObjectURL(blobUrl);
//     } catch (error) {
//       console.error('Download error:', error);
//     }
//   };

//   return (
//     <div className="space-y-4">
//       <div className="relative aspect-video rounded-lg overflow-hidden">
//         <video
//           src={videoUrl}
//           controls
//           className="w-full h-full"
//           preload="metadata"
//         />
//       </div>
//       <button
//         onClick={handleDownload}
//         className="w-full py-2 px-4 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
//       >
//         Download
//       </button>
//     </div>
//   )
// }

// export default function HomePage() {
//   const { user, isLoading, fetchUser } = useUserStore()

//   useEffect(() => {
//     fetchUser()
//   }, [fetchUser])

//   if (isLoading) {
//     return (
//       <div className="min-h-screen bg-[#161622] p-6 pb-24">
//         <div className="flex justify-between items-center mb-8">
//           <ThemeToggle />
//           <UserMenu />
//         </div>
//         <div className="flex items-center justify-center h-[calc(100vh-200px)]">
//           <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-[#0066FF] border-r-transparent"></div>
//         </div>
//       </div>
//     )
//   }

//   if (!user) {
//     return (
//       <div className="min-h-screen bg-[#161622] p-6 pb-24">
//         <div className="flex justify-between items-center mb-8">
//           <ThemeToggle />
//           <UserMenu />
//         </div>
//         <div className="flex items-center justify-center h-[calc(100vh-200px)]">
//           <Card className="p-6 bg-[#1E1E2D]">
//             <p className="text-white">Failed to load user data. Please try again later.</p>
//           </Card>
//         </div>
//       </div>
//     )
//   }

//   return (
//     <div className="min-h-screen bg-[#161622] p-6 pb-24">
//       <div className="flex justify-between items-center mb-8">
//         <ThemeToggle />
//         <UserMenu />
//       </div>

//       <div className="space-y-6 max-w-md mx-auto">
//         <AnalysisRadarChart
//           data={{
//             angle: user.elbowAngleScore,
//             stability: user.armStabilityScore,
//             speed: user.armSpeedScore,
//           }}
//         />
//         <ScoreChart />
//         <StatsSection
//           scoreStats={[
//             { label: "Average Score", value: user.totalPoseAvgscore.toFixed(1), color: "#2D3EF9" },
//             { label: "High Score", value: user.totalPoseHighscore.toFixed(1), color: "#2D3EF9" },
//           ]}
//           performanceStats={[
//             { label: "Angle", value: user.elbowAngleScore.toFixed(1), color: "#9CB1D1" },
//             { label: "Stability", value: user.armStabilityScore.toFixed(1), color: "#9CB1D1" },
//             { label: "Speed", value: user.armSpeedScore.toFixed(1), color: "#9CB1D1" },
//           ]}
//         />
//         <BowlingStats
//           stats={[
//             { label: "Average Score", value: user.avgBowlingScore.toString(), color: "#ECE929" },
//             { label: "Current Score", value: user.currBowlingScore.toString(), color: "#ECE929" },
//           ]}
//         />
//         <HighlightPlayer 
//           videoUrl={user.highlight} 
//           nickname={user.nickname}
//         />
//       </div>

//       <NavigationBar />
//     </div>
//   )
// }


"use client"

import dynamic from 'next/dynamic'
import { useEffect } from "react"
import { useUserStore } from "@/lib/user"

// Dynamic imports
const ThemeToggle = dynamic(() => import("@/components/ui/theme-toggle"), { ssr: false })
const UserMenu = dynamic(() => import("@/components/user-menu").then(mod => mod.UserMenu), { ssr: false })
const NavigationBar = dynamic(() => import("@/components/navigation-bar").then(mod => mod.NavigationBar), { ssr: false })
const AnalysisRadarChart = dynamic(() => import("@/components/charts/radar-chart").then(mod => mod.AnalysisRadarChart), { ssr: false })
const ScoreChart = dynamic(() => import("@/components/charts/score-chart").then(mod => mod.ScoreChart), { ssr: false })
const StatsSection = dynamic(() => import("@/components/statistics/stats-section").then(mod => mod.StatsSection), { ssr: false })
const BowlingStats = dynamic(() => import("@/components/statistics/bowling-stats").then(mod => mod.BowlingStats), { ssr: false })
const Card = dynamic(() => import("@/components/ui/card").then(mod => mod.Card), { ssr: false })

interface HighlightPlayerProps {
    videoUrl: string;
    nickname: string;
}

const HighlightPlayer = ({ videoUrl, nickname }: HighlightPlayerProps) => {
    const handleDownload = async () => {
        try {
            const response = await fetch(videoUrl);
            if (!response.ok) throw new Error("Network response was not ok");

            const blob = await response.blob();
            const blobUrl = URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = blobUrl;
            a.download = `highlight-${nickname}.mp4`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(blobUrl);
        } catch (error) {
            console.error("Download error:", error);
        }
    };

    return (
        <div className="space-y-4">
            <div className="relative aspect-video rounded-lg overflow-hidden">
                <video
                    src={videoUrl}
                    controls
                    className="w-full h-full"
                    preload="metadata"
                />
            </div>
            <button
                onClick={handleDownload}
                className="w-full py-2 px-4 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
            >
                Download
            </button>
        </div>
    )
}

export default function HomePage() {
    const { user, isLoading, fetchUser } = useUserStore()

    useEffect(() => {
        fetchUser()
    }, [fetchUser])

    if (isLoading) {
        return (
            <div className="min-h-screen bg-[#161622] p-6 pb-24">
                <div className="flex justify-between items-center mb-8">
                    <ThemeToggle />
                    <UserMenu />
                </div>
                <div className="flex items-center justify-center h-[calc(100vh-200px)]">
                    <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-[#0066FF] border-r-transparent"></div>
                </div>
            </div>
        )
    }

    if (!user) {
        return (
            <div className="min-h-screen bg-[#161622] p-6 pb-24">
                <div className="flex justify-between items-center mb-8">
                    <ThemeToggle />
                    <UserMenu />
                </div>
                <div className="flex items-center justify-center h-[calc(100vh-200px)]">
                    <Card className="p-6 bg-[#1E1E2D]">
                        <p className="text-white">Failed to load user data. Please try again later.</p>
                    </Card>
                </div>
            </div>
        )
    }

    return (
        <div className="min-h-screen bg-[#161622] p-6 pb-24">
            <div className="flex justify-between items-center mb-8">
                <ThemeToggle />
                <UserMenu />
            </div>

            <div className="space-y-6 max-w-md mx-auto">
                <AnalysisRadarChart
                    data={{
                        angle: user.elbowAngleScore,
                        stability: user.armStabilityScore,
                        speed: user.armSpeedScore,
                    }}
                />
                <ScoreChart />
                <StatsSection
                    scoreStats={[
                        { label: "Average Score", value: user.totalPoseAvgscore.toFixed(1), color: "#2D3EF9" },
                        { label: "High Score", value: user.totalPoseHighscore.toFixed(1), color: "#2D3EF9" },
                    ]}
                    performanceStats={[
                        { label: "Angle", value: user.elbowAngleScore.toFixed(1), color: "#9CB1D1" },
                        { label: "Stability", value: user.armStabilityScore.toFixed(1), color: "#9CB1D1" },
                        { label: "Speed", value: user.armSpeedScore.toFixed(1), color: "#9CB1D1" },
                    ]}
                />
                <BowlingStats
                    stats={[
                        { label: "Average Score", value: user.avgBowlingScore.toString(), color: "#ECE929" },
                        { label: "Current Score", value: user.currBowlingScore.toString(), color: "#ECE929" },
                    ]}
                />
                <HighlightPlayer 
                    videoUrl={user.highlight}
                    nickname={user.nickname}
                />
            </div>

            <NavigationBar />
        </div>
    )
}




// "use client"

// import axios from 'axios'
// import { useEffect } from "react"
// import ThemeToggle from "@/components/ui/theme-toggle"
// import { UserMenu } from "@/components/user-menu"
// import { NavigationBar } from "@/components/navigation-bar"
// import { AnalysisRadarChart } from "@/components/charts/radar-chart"
// import { ScoreChart } from "@/components/charts/score-chart"
// import { StatsSection } from "@/components/statistics/stats-section"
// import { BowlingStats } from "@/components/statistics/bowling-stats"
// import { useUserStore } from "@/lib/user"
// import { Card } from "@/components/ui/card"

// interface HighlightPlayerProps {
//     videoUrl: string;
//     onDownload: () => void;
// }

// const HighlightPlayer = ({ videoUrl, onDownload }: HighlightPlayerProps) => {
//     const handleDownload = () => {
//         axios({
//             url: videoUrl,
//             method: 'GET',
//             responseType: 'blob'
//         })
//         .then(response => {
//             const blob = new Blob([response.data], { type: 'video/mp4' });
//             const blobUrl = URL.createObjectURL(blob);
//             const a = document.createElement('a');
//             a.href = blobUrl;
//             a.download = 'video.mp4';
//             document.body.appendChild(a);
//             a.click();
//             document.body.removeChild(a);
//             URL.revokeObjectURL(blobUrl);
//         })
//         .catch(error => {
//             console.error('Download error:', error);
//         });
//     };

//     return (
//         <div className="space-y-4">
//             <div className="relative aspect-video rounded-lg overflow-hidden">
//                 <video
//                     src={videoUrl}
//                     controls
//                     className="w-full h-full"
//                     preload="metadata"
//                 />
//             </div>
//             <button
//                 onClick={handleDownload}
//                 className="w-full py-2 px-4 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
//             >
//                 Download
//             </button>
//         </div>
//     )
// }

// export default function HomePage() {
//     const { user, isLoading, fetchUser } = useUserStore()

//     useEffect(() => {
//         fetchUser()
//     }, [fetchUser])

//     if (isLoading) {
//         return (
//             <div className="min-h-screen bg-[#161622] p-6 pb-24">
//                 <div className="flex justify-between items-center mb-8">
//                     <ThemeToggle />
//                     <UserMenu />
//                 </div>
//                 <div className="flex items-center justify-center h-[calc(100vh-200px)]">
//                     <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-[#0066FF] border-r-transparent"></div>
//                 </div>
//             </div>
//         )
//     }

//     if (!user) {
//         return (
//             <div className="min-h-screen bg-[#161622] p-6 pb-24">
//                 <div className="flex justify-between items-center mb-8">
//                     <ThemeToggle />
//                     <UserMenu />
//                 </div>
//                 <div className="flex items-center justify-center h-[calc(100vh-200px)]">
//                     <Card className="p-6 bg-[#1E1E2D]">
//                         <p className="text-white">Failed to load user data. Please try again later.</p>
//                     </Card>
//                 </div>
//             </div>
//         )
//     }

//     const handleDownload = () => {
//         const link = document.createElement("a")
//         link.href = user.highlight
//         link.download = `highlight-${user.nickname}.mp4`
//         document.body.appendChild(link)
//         link.click()
//         document.body.removeChild(link)
//     }

//     return (
//         <div className="min-h-screen bg-[#161622] p-6 pb-24">
//             <div className="flex justify-between items-center mb-8">
//                 <ThemeToggle />
//                 <UserMenu />
//             </div>

//             <div className="space-y-6 max-w-md mx-auto">
//                 <AnalysisRadarChart
//                     data={{
//                         angle: user.elbowAngleScore,
//                         stability: user.armStabilityScore,
//                         speed: user.armSpeedScore,
//                     }}
//                 />
//                 <ScoreChart />
//                 <StatsSection
//                     scoreStats={[
//                         { label: "Average Score", value: user.totalPoseAvgscore.toFixed(1), color: "#2D3EF9" },
//                         { label: "High Score", value: user.totalPoseHighscore.toFixed(1), color: "#2D3EF9" },
//                     ]}
//                     performanceStats={[
//                         { label: "Angle", value: user.elbowAngleScore.toFixed(1), color: "#9CB1D1" },
//                         { label: "Stability", value: user.armStabilityScore.toFixed(1), color: "#9CB1D1" },
//                         { label: "Speed", value: user.armSpeedScore.toFixed(1), color: "#9CB1D1" },
//                     ]}
//                 />
//                 <BowlingStats
//                     stats={[
//                         { label: "Average Score", value: user.avgBowlingScore.toString(), color: "#ECE929" },
//                         { label: "Current Score", value: user.currBowlingScore.toString(), color: "#ECE929" },
//                     ]}
//                 />
//                 <HighlightPlayer videoUrl={user.highlight} onDownload={handleDownload} />
//             </div>

//             <NavigationBar />
//         </div>
//     )
// }